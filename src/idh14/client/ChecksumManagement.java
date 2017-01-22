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

    private String absolutePath;
    private ArrayList<NewFileHandler> fileList;
    private DiskHandler diskHandler;

    public ChecksumManagement(String absolutePath, DiskHandler diskHandler) throws FileNotFoundException, IOException, EOFException, ClassNotFoundException {
        this.absolutePath = absolutePath;
        fileList = new ArrayList<NewFileHandler>();
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
                FileInputStream fis = new FileInputStream(absolutePath);
                ObjectInputStream ois = new ObjectInputStream(fis);
                ArrayList<NewFileHandler> fileList = (ArrayList<NewFileHandler>) ois.readObject();
                FileOutputStream fos = new FileOutputStream(absolutePath);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(fileList);
                oos.close();
                System.out.println("Aantal objecten in arraylist :" + fileList.size());
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
        NewFileHandler arrayFile = null;
        LocalFileWrapper local = null;

        if (length < 10) {

            System.out.println("FILE-LENGTH < 10 // Voegen we eerst een lege array aan de file toe");
            // Eerst iets aanmaken anders krijgen we een NULLpointer op de objectinputstream
            FileOutputStream fos = new FileOutputStream(absolutePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(fileList);
            oos.close();

            // Nu array vullen
            FileInputStream fis = new FileInputStream(absolutePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<NewFileHandler> fileList = (ArrayList<NewFileHandler>) ois.readObject();

            // Array Opslaan 1e keer.
            fileList.add(fileFromServer);
            System.out.println("En hier de 1e ADD ooit !");
            add = false;
            FileOutputStream fos2 = new FileOutputStream(absolutePath);
            ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
            oos2.writeObject(fileList);
            oos2.close();

        } else {

            // Array lokaal aanwezig. Dus nu nadenken over het toevoegen
            FileInputStream fis = new FileInputStream(absolutePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<NewFileHandler> fileList = (ArrayList<NewFileHandler>) ois.readObject();
            System.out.println("Aantal objecten in arraylist :" + fileList.size());

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
                    if(function == "put"){
                        System.out.println("PUT functie --> checksum update !");
                        update = true;
                        message = false;
                    }
                }
            }

            while (add) {
                System.out.println("ADD LOOP");
                fileList.add(fileFromServer);

                FileOutputStream fos2 = new FileOutputStream(absolutePath);
                ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
                oos2.writeObject(fileList);
                oos2.close();
                add = false;
            }

            while (update) {

                if (!arrayFile.getOriginalChecksum().equals(fileFromServer.getOriginalChecksum())) {
                    System.out.println("SERVER FILE = nieuwer .. UPDATE LOKAAL");
                    fileList.remove(arrayFile);
                    fileList.add(fileFromServer);

                } else {
                    System.out.println("FILE-EXISTS = TRUE // Maar lokale en serverfile checksum komen overeen, dus doe NIETS");
                }

                FileOutputStream fos2 = new FileOutputStream(absolutePath);
                ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
                oos2.writeObject(fileList);
                oos2.close();
                update = false;

            }

            while (message) {
                System.out.println("Message to user ?");
                message = false;
                writePermitted = false;
            }

            FileOutputStream fos2 = new FileOutputStream(absolutePath);
            ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
            oos2.writeObject(fileList);
            oos2.close();

        }

        return writePermitted;
    }
    
    public String getOriginalChecksumFromFile(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {

        File f = new File(absolutePath);
        long length = f.length();

        if (length < 10) {

            System.out.println("FILE-LENGTH < 10 // Voegen we eerst een lege array aan de file toe");
            // Eerst iets aanmaken anders krijgen we een NULLpointer op de objectinputstream
            FileOutputStream fos = new FileOutputStream(absolutePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(fileList);
            oos.close();
        }
        
        
        FileInputStream fis = new FileInputStream(absolutePath);
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<NewFileHandler> fileList = (ArrayList<NewFileHandler>) ois.readObject();
        String originalChecksum = null;
        
        for (NewFileHandler arrayFile : fileList) {
            if(arrayFile.getFileName().equals(filename)){
                originalChecksum = arrayFile.getOriginalChecksum();
                            System.out.println("Filename : " + arrayFile.getFileName());
            System.out.println("Org - checksum : " + arrayFile.getOriginalChecksum());
            }

            
        }
        
        return originalChecksum;
    }
    
}
