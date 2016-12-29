package idh14.protocol;

import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONTokener;

public class Request {

    public enum Type {
        LIST, GET, PUT, DELETE;
    }

    public static final String PROTOCOL = "idh14sync";

    public static final String VERSION = "1.0";

    public static final String LF = "\n";

    private final Type type;

    private String protocol;

    private String version;

    /**
     * Inhoud van de response.
     */
    private JSONObject body;

    protected Request(Type type) {
        this.type = type;
        body = null;
    }

    public Request(Type type, JSONObject body) {
        this.type = type;
        this.protocol = PROTOCOL;
        this.version = VERSION;
        this.body = body;
    }

    public Type getType() {
        return type;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getVersion() {
        return version;
    }

    public JSONObject getBody() {
        return body;
    }

    public static Request unMarshallRequest(BufferedReader reader) throws IOException {

        Request result;

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

            // Bepaal het type request.
            String r = s[0];
            Type t = Type.valueOf(r);
            result = new Request(t);

            // Bepaal het protocol en het versienummer.
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

    @Override
    public String toString() {
        String result = type.toString() + ' ' + PROTOCOL + '/' + VERSION + LF;
        result += LF;
        result += body;
        result += LF;
        return result;
    }

}
