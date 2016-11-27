package idh14.protocol;

public class File {

	private String filename;
	
	private String checksum;
	
	private String original_checksum;
	
	private String content;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getOriginal_checksum() {
		return original_checksum;
	}

	public void setOriginal_checksum(String original_checksum) {
		this.original_checksum = original_checksum;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}
