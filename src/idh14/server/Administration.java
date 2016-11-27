package idh14.server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;

public class Administration {

	/**
	 * De bestandsadministratie komt neer op een hash map met bestandsnamen als key en 
	 * een LIFO-ordered Set (bestaat dat?of wordt het een List?) met daarin alle hashes
	 * waarmee dat bestand bekend is geweest. Deze administratie moet worden opgeslagen
	 * om de historie veilig te stellen als de server (tijdelijk) wordt afgesloten.
	 */
	private final Map<String, Stack<String>> files = new HashMap<>();
	
	public void addFile(String filename, String hash) {
		if (files.containsKey(filename)) {
			Stack hashes = files.get(filename);
			hashes.add(hash);
		}
		else {
			Stack<String> firstHash = new Stack<String>();
			firstHash.push(hash);
			files.put(filename, firstHash);
		}
	}
	
	public String getHash(String filename) {
		if (files.containsKey(filename)) {
			Stack<String> hashes = files.get(filename);
			return hashes.peek();
		}
		return null;
	}

	public void removeFile(String filename) {
		
	}
	
	/**
	 * De administratie wordt ingelezen vanuit een FileSharing-bestand.
	 * @param file FileSharing-bestand
	 */
	public void loadFromFile(File file) {
		
	}
	
	/**
	 * De administratie wordt wegggeschreven naar een FileSharing-bestand.
	 * @param file FileSharing-bestand
	 */
	public void saveToFile(File file) {
		
	}
	
}
