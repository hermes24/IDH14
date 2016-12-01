package idh14.protocol;

import org.json.JSONObject;

public class Response {

	public enum Status {
		OK(200), GEEN_IDEE(400), NOT_FOUND(404), CONFLICT(412);
		
		@SuppressWarnings("unused")
		private final int code;

		Status(int code) {
			this.code = code;
		}
	}

	public static final String ID = "RESPONSE";

	public static final String PROTOCOL = "idh14sync";

	public static final String VERSION = "1.0";

	public static final String LF = "\n";

	/**
	 * Status code.
	 */
	private final Status status;
	
	/**
	 * Inhoud van de response.
	 */
	private final JSONObject body;

	/**
	 * Request.
	 * 
	 * @param status
	 *            statuscode
	 * @param body
	 *            inhoud
	 */
	public Response(Status status, JSONObject body) {
		this.status = status;
		this.body = body;
	}

	/**
	 * Getter voor status.
	 * 
	 * @return status
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * Getter voor de body.
	 */
	public JSONObject getBody() {
		return body;
	}

	@Override
	public String toString() {
		String result = ID + ' ' + PROTOCOL + '/' + VERSION + LF;
		result += body;
		result += LF;
		return result;
	}
}
