package idh14.server;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Storage {

	/**
	 * De uiteindelijke naam van de folder.
	 */
	private final String directory;
	
	private final MessageDigest messageDigest;

	public Storage(String directory) throws IOException, NoSuchAlgorithmException {
		assert(!directory.isEmpty());
		this.directory = directory;
		// Controleer of we in de opgegeven directory mogen lezen en schrijven.
		// TODO: dit nog verder uitwerken: welke permissies moet ik aan Eclipse toevoegen?
		//File t = new File(directory + "/test.txt");
		//if (!t.canRead() || !t.canWrite())
		//	throw new IOException("Geen lees- en/of schrijfrechten in de opgegeven folder.");
		
		// Controleer of we de interne administratie van deze directory kunnen
		// benaderen. Zo niet: einde oefening.
		this.messageDigest = MessageDigest.getInstance("SHA-1");
	}

	public ArrayList<FileWrapper> getFileWrappers() throws IOException {
		ArrayList<FileWrapper> result = new ArrayList<FileWrapper>();
		File d = new File(directory);
		File[] l = d.listFiles();
		// TODO: volgens mij crasht e.e.a. als de dir leeg is. Even testen?
		for (File f : l) {
			FileWrapper w = new FileWrapper(f, messageDigest);
			w.calculateChecksum();
			result.add(w);
		}
		return result;
	}

	public String getDirectory() {
		return directory;
	}
	
}
