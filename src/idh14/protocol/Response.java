package idh14.protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONTokener;

public class Response {

	public enum Status {
		OK(200), GEEN_IDEE(400), NOT_FOUND(404), CONFLICT(412), ERROR(500);
		
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
	private JSONObject body;
        
        private String protocol;

        private String version;

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
         
        Response result;

        try {
            // Lees door tot er ofwel een niet-lege regel is gelezen of dat het eind van de stream
            // is bereikt (client heeft verbinding verbroken).
            String l = "";
            do {
                l = reader.readLine();
                if (l == null) {
                    return null;
                }
            } while (l.trim().equals(""));

            // We nemen aan dat de regel die nu gelezen gaat worden de protocol header is.
            String[] s = l.split(" ", 2);

            // Maak leeg response object.
            JSONObject b = new JSONObject();
            result = new Response(b);
            
            // Bepaal het protocol en het versienummer + vul object met strings uit buffer
            String[] p = s[1].split("/");
            result.protocol = p[0];
            if (!result.protocol.equals(PROTOCOL)) {
                throw new IOException("Invalid protocol " + result.protocol + " specified.");
            }
            result.version = p[1];
            if (!result.version.equals(VERSION)) {
                throw new IOException("Invalid version " + result.version + " specified.");
            }

            // Lees de lege regel na de protocol header.
            reader.readLine();

            // Lees de (lege) JSON request body.
            JSONTokener tokener = new JSONTokener(reader);
            result.body = (JSONObject) tokener.nextValue();

        } catch (Exception e) {
            throw new IOException("Data error while reading from socket: " + e.getMessage());
        }

        return result;
        
        

    }

}
