package idh14.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class ListResponse extends Response {
	
	private List<File> files = new ArrayList<>();

	public ListResponse(int status) {
		super(status);
	}
	
	public void addFile(File file) {
		files.add(file);
	}
	
	@Override
	public String toString() {
		String result = super.toString();
		JSONObject o = new JSONObject();
		o.put("status", STATUS_OK);
		if (!files.isEmpty()) {
//			JSONObject fl = o.append("files", getStatus());
//			for (File f : files) {
//				fl.put("filename", f.getFilename());
//				fl.put("checksum", f.getChecksum());
//			}
		}
		result += o;
		result += LF;
		return result;
	}
}
