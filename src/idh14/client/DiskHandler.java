package idh14.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class DiskHandler {

	/**
	 * De uiteindelijke naam van de folder.
	 */
	private final String directory;
	private final MessageDigest messageDigest;

	public DiskHandler(String directory) throws IOException, NoSuchAlgorithmException {
		assert(!directory.isEmpty());
		this.directory = directory;
		this.messageDigest = MessageDigest.getInstance("SHA-1");
	}

	public LocalFileWrapper getFileWrapper(String filename) throws IOException {
		File f = new File(directory + File.separator + filename);
		LocalFileWrapper fileHandler = new LocalFileWrapper(f, messageDigest);
		fileHandler.calculateChecksum();
		return fileHandler;
	}
        
        public long getChecksumIntegrity() throws IOException{
            long numberOfFiles = Files.list(Paths.get(this.directory)).count();
            return numberOfFiles;
        }
        
        public ArrayList<LocalFileWrapper> getFileWrappers() throws IOException{
            	ArrayList<LocalFileWrapper> result = new ArrayList<>();
		File d = new File(directory);
		File[] l = d.listFiles();
		// TODO: Crasht als de dir leeg is of niet bestaat (nog uitzoeken).
		for (File f : l) {
			LocalFileWrapper w = new LocalFileWrapper(f, messageDigest);
			w.calculateChecksum();
			result.add(w);
		}
		return result;
        }

	public void deleteFile(String filename) throws IOException {
		File f = new File(directory + File.separator + filename);
		//f.delete();
		System.out.println("Aargh!! Bestand is weg! (maar niet heus)");
	}
	
	public String getDirectory() {
		return directory;
	}
	
}
