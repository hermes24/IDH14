package idh14.protocol;

public abstract class Request {

	public static final String PROTOCOL = "idh14sync";

	public static final String VERSION = "1.0";

	public static final String LF = "\n";
	
	private final RequestType type;
	
	protected Request(RequestType type) {
		this.type = type;
	}
	
	public RequestType getType() {
		return type;
	}
	
}
