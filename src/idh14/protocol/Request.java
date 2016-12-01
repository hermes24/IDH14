package idh14.protocol;

public class Request {

	public enum Type {
		LIST, GET, PUT, DELETE;
	}
	
	public static final String PROTOCOL = "idh14sync";

	public static final String VERSION = "1.0";

	public static final String LF = "\n";
	
	private final Type type;
	
	protected Request(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
}
