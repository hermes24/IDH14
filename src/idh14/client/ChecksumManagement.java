package idh14.client;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ChecksumManagement {

    public final BufferedReader reader;
    private String absolutePath;
    private ArrayList<NewFileHandler> fileList;

    public ChecksumManagement(String absolutePath) throws FileNotFoundException, IOException, EOFException, ClassNotFoundException {
        this.absolutePath = absolutePath;
        fileList = new ArrayList<>();
        load();
        reader = new BufferedReader(new FileReader(absolutePath));
        
        
    }

    public void load() throws IOException, ClassNotFoundException {
        
        // Vereist om de 1e x file te lezen en te voorzien van lege arraylist.
        // Met lege file klap de hele bende.

        try {
            File f = new File(absolutePath);
            long length = f.length();
            boolean exists = f.exists();
            if(!exists){
                f.createNewFile();
            }
            if (length > 1) {
                FileInputStream fis = new FileInputStream(absolutePath);
                ObjectInputStream ois = new ObjectInputStream(fis);
                ArrayList<NewFileHandler> fileList = (ArrayList<NewFileHandler>) ois.readObject();
            }

            FileOutputStream fos = new FileOutputStream(absolutePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(fileList);
            oos.close();

            for (NewFileHandler element : fileList) {
                System.out.println("load functie: Huidige elementen :" + element.getFileName());
            }
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    public boolean fileExistsInList(NewFileHandler file) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(absolutePath);
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<NewFileHandler> fileList = (ArrayList<NewFileHandler>) ois.readObject();

        boolean fileExistsInList = false;

        for (NewFileHandler element : fileList) {
            if (element.getFileName().equals(file.getFileName())) {
                System.out.println("FILE-EXISTS = TRUE // Filename in list : " + element.getFileName() + " Filename in opgehaalde file : " + file.getFileName());
                fileExistsInList = true;
            }
        }
        return fileExistsInList;
    }

    public void updateFile(NewFileHandler fileFromServer, String checksumLocalFile, String checksumServerFile) {
        
        for (NewFileHandler newFileHandler : fileList) {
            if(!newFileHandler.getOriginalChecksum().equals(fileFromServer.getOriginalChecksum())){
                // HIER MOET NOG DE VERGELIJKING NAAR CHECKSOMLOKAAL BIJ 
                System.out.println("Checksum is anders dus file lokaal bijwerken");
            }
        }
        

        // filename wel aanwezig, dan controle op checksum. Checksum lokaal hetzelfde als op server. NIKS DOEN.
        // filename wel aanwezig, dan controle op checksum. Checksum lokaal + checksum admin zelfde, maar server anders. UPDATE LOKAAL.
        // filename wel aanwezig, dan controle op checksum. Checksum lokaal + checksum admin anders, en server niet zelfde als lokaal. CONFLICT voor client.
        //Eerst checken of lokale checksum gelijk is aan die in de arraylist.
        //Mogelijk is de lokale tussendoor gewijzigd. En dit mag niet zonder update vanuit server.
    }

    void addObjectFile(NewFileHandler f) throws IOException {
        FileOutputStream fos = new FileOutputStream(absolutePath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        for (NewFileHandler element : fileList) {
            System.out.println("AddObjectFile : " + element.getFileName());
        }

        fileList.add(f);
        oos.writeObject(fileList);
        oos.close();

    }

}
