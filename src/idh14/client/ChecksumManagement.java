package idh14.client;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public final class ChecksumManagement {

    private final String absolutePath;
    private ArrayList<NewFileHandler> fileList;
    private final DiskHandler diskHandler;

    public ChecksumManagement(String absolutePath, DiskHandler diskHandler) throws FileNotFoundException, IOException, EOFException, ClassNotFoundException {
        this.absolutePath = absolutePath;
        fileList = new ArrayList<>();
        load();
        this.diskHandler = diskHandler;
    }

    public void load() throws FileNotFoundException, IOException, ClassNotFoundException {

        try {
            File f = new File(absolutePath);
            long length = f.length();
            boolean exists = f.exists();

            // File niet aanwezig, dan aanmaken
            if (!exists) {
                f.createNewFile();
            }
            // Objectinputstream werkt niet op niet bestaande of lege file.
            // Checken of er iets in de file zit, zoja dan lezen.
            if (length > 1) {
                getArrayFromDisk();
            }
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    public boolean addOrUpdate(NewFileHandler fileFromServer, String function) throws FileNotFoundException, IOException, ClassNotFoundException {

        File f = new File(absolutePath);
        long length = f.length();
        boolean add = true;
        boolean update = false;
        boolean message = false;
        boolean writePermitted = true;
        boolean checksumIntegrityCompromised = false;
        NewFileHandler arrayFile = null;
        LocalFileWrapper local = null;

        if (fileList.size() != diskHandler.getChecksumIntegrity()) {
            checksumIntegrityCompromised = true;

            // Nog iets bouwen dat ik checksum history bijwerk als er ook maar iets gebeurt.
            // Door bijvoorbeeld onverwacht lokaal verwijderen, etc ..
        }

        for (NewFileHandler tempFile : fileList) {

            // Aanwezig lokaal = UPDATE anders ADD
            if (tempFile.getFileName().equals(fileFromServer.getFileName())) {
                add = false;
                arrayFile = tempFile;

                local = diskHandler.getFileWrapper(tempFile.getFileName());
                if (local.getChecksum().equals(arrayFile.getOriginalChecksum())) {

                    System.out.println("LOKAAL & ARRAY = ZELFDE  -- > lokaal updaten toegestaan.");
                    update = true;
                    message = false;

                } else {
                    System.out.println("LOKAAL & ARRAY = AFWIJKEND .. USER interactie gewenst");
                    message = true;
                }
                if ("put".equals(function)) {
                    System.out.println("PUT functie --> checksum update !");
                    update = true;
                    message = false;
                }
            }
        }

        while (add) {
            System.out.println("ADD LOOP");
            fileList.add(fileFromServer);
            putArrayToDisk();
            add = false;
            System.out.println("Aantal objecten in arraylist NA ADD: " + fileList.size());
            System.out.println("Aantal files in client folder NA ADD: " + diskHandler.getChecksumIntegrity());
        }

        while (update) {

            if (!arrayFile.getOriginalChecksum().equals(fileFromServer.getOriginalChecksum())) {
                System.out.println("SERVER FILE OF UPDATE FILE = nieuwer .. UPDATE Administratie");
                fileList.remove(arrayFile);
                fileList.add(fileFromServer);

            } else {
                System.out.println("FILE-EXISTS = TRUE // Maar lokale en serverfile checksum komen overeen, dus doe NIETS");
            }

            putArrayToDisk();
            update = false;
            System.out.println("Aantal objecten in arraylist NA UPDATE: " + fileList.size());
            System.out.println("Aantal files in client folder NA UPDATE: " + diskHandler.getChecksumIntegrity());

        }

        while (message) {
            System.out.println("Message to user ?");
            message = false;
            writePermitted = false;
        }

        return writePermitted;
    }

    public String getOriginalChecksumFromFile(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {

        System.out.println("getOriginalChecksumFromFile OPERATIE");
        String originalChecksum = null;

        for (NewFileHandler arrayFile : fileList) {
            if (arrayFile.getFileName().equals(filename)) {
                originalChecksum = arrayFile.getOriginalChecksum();
                System.out.println("Filename : " + arrayFile.getFileName());
                System.out.println("Org - checksum : " + arrayFile.getOriginalChecksum());
            }
        }
        putArrayToDisk();
        return originalChecksum;
    }

    public ArrayList<NewFileHandler> getArrayFromDisk() throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(absolutePath);
        ObjectInputStream ois = new ObjectInputStream(fis);
        fileList = (ArrayList<NewFileHandler>) ois.readObject();
        System.out.println("getArrayFromDisk OPERATIE");
        return fileList;
    }

    public void putArrayToDisk() throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(absolutePath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(fileList);
        System.out.println("putArrayToDisk OPERATIE");
        oos.close();
    }

}
