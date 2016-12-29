package idh14.protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import org.json.JSONObject;

public class Response {

	public enum Status {
		OK(200), GEEN_IDEE(400), NOT_FOUND(404), CONFLICT(412);
		
		private final int code;

		Status(int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
	}

	public static final String ID = "RESPONSE";

	public static final String PROTOCOL = "idh14sync";

	public static final String VERSION = "1.0";

	public static final String LF = "\n";
	
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
	public Response(JSONObject body) {
		this.body = body;
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
	
	public void marshall(BufferedWriter writer) throws IOException {		
		writer.write(ID + ' ' + PROTOCOL + '/' + VERSION + LF + LF);
		writer.write(body.toString(4) + LF);
		writer.flush();
	}
        
        public static Response unMarshallResponse(BufferedReader reader) throws IOException {

        // even snelle methode gemaakt om te testen of output werkt. 
        // verdere implementatie moet nog ingevuld worden.
            
        Response result;
        JSONObject b = new JSONObject();
        result = new Response(b);
        
        System.out.println("unmarshall method in response .. " + reader.readLine().toString());
        return result;

    }

}
