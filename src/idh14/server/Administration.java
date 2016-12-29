package idh14.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Administration {

	private final BufferedReader reader;

	private final BufferedWriter writer;

	private final HashMap<String, ArrayList<String>> history;

	public Administration(String absolutePath) throws FileNotFoundException, IOException {
		File f = new File(absolutePath);
		reader = new BufferedReader(new FileReader(f));
		writer = new BufferedWriter(new FileWriter(f));
		history = new HashMap<String, ArrayList<String>>();
	}

	public void load() throws IOException {
		while (reader.ready()) {
			String d = reader.readLine();
		}
	}

	public String[] getChecksumHistory(String filename) throws IllegalArgumentException {
		if (!history.containsKey(filename))
			throw new IllegalArgumentException("Onbekend bestand.");
		ArrayList<String> h = history.get(filename);
		String[] result = new String[h.size()];
		int i = 0;
		for (String s : h)
			result[i++] = s;
		return result;
	}

	public void addFile(String filename, String checksum) throws IllegalArgumentException {
		if (history.containsKey(filename))
			throw new IllegalArgumentException("Bestand bestaat al.");
		ArrayList<String> l = new ArrayList<String>();
		l.add(checksum);
		history.put(filename, l);
	}

	public void updateFile(String filename) throws IllegalArgumentException {
		if (!history.containsKey(filename))
			throw new IllegalArgumentException("Onbekend bestand.");
		// TODO: implementeren
	}

	public void removeFile(String filename) throws IllegalArgumentException {
		if (!history.containsKey(filename))
			throw new IllegalArgumentException("Onbekend bestand.");
		// TODO: implementeren
	}

	public void save() throws IOException {
		for (String k : history.keySet()) {
			String r = k;
			ArrayList<String> l = history.get(k);
			for (String c : l)
				r += ' ' + c;
			writer.write(r);
		}
		writer.flush();
	}

}
