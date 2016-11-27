package idh14.server;

public class Folder {

	private final String folderPath;
	
	private final Administration admin;
	
	public Folder(String folderPath) {
		this.folderPath = folderPath;
		admin = new Administration();
	}
	
	public void update() {
		// Dir lezen en alle files toevoegen.
	}
	
	/**
	 * Getter voor bestandsmap.
	 * @return bestandsmap
	 */
	public String getFolderPath() {
		return folderPath;
	}
	
}
