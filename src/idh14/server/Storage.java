package idh14.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Storage {

	private final String directory;

	public Storage(String directory) {
		this.directory = directory;
		// TODO: testen of we hier wel lees- en schrijfrechten hebben -> IOException
	}

	public ArrayList<String> getFiles() {
		// TODO: File-objecten teruggeven ipv Strings?
		ArrayList<String> result = new ArrayList<String>();
		File d = new File(directory);
		File[] l = d.listFiles();
		for (File f : l)
			result.add(f.getName());
		return result;
	}

	public FileInputStream getInputStream(String filename) throws FileNotFoundException {
		File f = new File(directory + '/' + filename);
		return new FileInputStream(f);
	}

	public FileOutputStream getOutputStream(String filename) throws FileNotFoundException {
		File f = new File(directory + '/' + filename);
		return new FileOutputStream(f);
	}

	public String getDirectory() {
		return directory;
	}
}
