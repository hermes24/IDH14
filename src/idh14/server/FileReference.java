package idh14.server;

import java.io.File;

public class FileReference {

	private final String filename;
	
	private String checksum;
	
	private String originalChecksum;
	
	private File file;
	
	public FileReference(String filename) {
		this.filename = filename;
		file = new File(filename);
	}

	public String getFilename() {
		return filename;
	}

	public String getChecksum() {
		return checksum;
	}

	public String getOriginalChecksum() {
		return originalChecksum;
	}
	
	// TODO: encapsuleren?
	public File getFile() {
		return file;
	}
	
}
