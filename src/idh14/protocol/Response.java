package idh14.protocol;

public abstract class Response {

	public static final String ID = "RESPONSE";

	public static final String PROTOCOL = "idh14sync";

	public static final String VERSION = "1.0";

	public static final String LF = "\n";
	
	public static final int STATUS_OK = 200;

	/**
	 * Status code.
	 */
	private final int status;

	/**
	 * Request.
	 * 
	 * @param status
	 *            statuscode
	 * @param body
	 *            inhoud
	 */
	protected Response(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return ID + ' ' + PROTOCOL + '/' + VERSION + LF + LF;
	}

	/**
	 * Getter voor status.
	 * 
	 * @return status
	 */
	public int getStatus() {
		return status;
	}

}
