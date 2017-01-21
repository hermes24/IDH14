package idh14.client;

//File Sharing
//
//Luc Hermes  |  Eric Marsilje  |  Joost van Stuijvenberg
//
//Avans Hogeschool Breda - IDH14
//November/December 2016
import idh14.protocol.Request;
import idh14.protocol.Response;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Base64;
import org.json.JSONObject;

/**
 *
 * @author luche
 */
public class ServerHandler implements Runnable {

    private final Socket socket;
    private String address;
    private int port;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final ClientUI clientui;
    private Thread runner;
    private boolean active;
    private JSONObject body;

    public ServerHandler(Socket socket, ClientUI clientui) throws IOException {
        assert (socket.isConnected());
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.clientui = clientui;
    }

    public void run() {
        while (!runner.isInterrupted()) {
        }
    }

    public void start() {
        assert (runner == null);
        runner = new Thread(this);
        runner.start();
        System.out.println("Server handler voor " + toString() + " is gestart.");
    }

    public void stop(boolean block) {
        assert (runner != null);
        assert (runner.isAlive());
        runner.interrupt();
        if (block) {
            while (!runner.isInterrupted());
        }
        System.out.println("Server handler voor " + toString() + " is gestopt.");
    }

    public Response getServerFileList() {

        JSONObject empty = new JSONObject();
        Response response = new Response(empty);
        active = true;

        while (active) {
            // Bouwen request met type LIST
            Request.Type type = Request.Type.LIST;
            Request r = new Request(type, empty);

            try {
                // Send request naar server
                writer.write(r.toString());
                writer.flush();
                active = false;

            } catch (IOException e) {
                System.err.println(e.getMessage());
                stop(true);
            }

            try {
                // Verwerk response ontvangen van server    
                response = new Response(empty).unMarshallResponse(reader);

            } catch (IOException e) {
                System.err.println(e.getMessage());
                stop(true);
            }
        }

        return response;

    }

    public void getFileFromServer(String request, String location) throws IOException {

        // TO DO: deels zelfde code als getFileList. 
        // Moet nieuwe functie voor komen om duplicatie te voorkomen
        JSONObject empty = new JSONObject();
        Response response = new Response(empty);
        active = true;
        while (active) {

            try {
                // Send request naar server
                writer.write(request);
                writer.flush();
                active = false;
                System.out.println(request);

            } catch (IOException e) {
                System.err.println(e.getMessage());
                stop(true);
            }

            try {
                // Verwerk response ontvangen van server    
                response = new Response(empty).unMarshallResponse(reader);

            } catch (IOException e) {
                System.err.println(e.getMessage());
                stop(true);
            }

            // Nieuwe file lokaal aanmaken
            FileOutputStream fos = null;

            try {
                // Decode inhoud 
                Base64.Decoder e = Base64.getDecoder();
                
                // File name moet nog naar Base64 
                fos = new FileOutputStream(location + response.getBody().get("filename").toString());
                byte[] buffer = e.decode(response.getBody().get("content").toString());
                fos.write(buffer);

            } catch (IOException e) {
                e.getMessage();
            } finally {
                if (fos != null) {
                    fos.close();

                }
            }
        }
    }
}
