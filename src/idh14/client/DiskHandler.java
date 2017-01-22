package idh14.client;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


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

	public LocalFileHandler getFileHandler(String filename) throws IOException {
		File f = new File(directory + File.separator + filename);
		LocalFileHandler fileHandler = new LocalFileHandler(f, messageDigest);
		fileHandler.calculateChecksum();
		return fileHandler;
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
