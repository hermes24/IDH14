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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
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
    private DiskHandler diskHandler;
    private String location = "C:\\client\\";
    private String managementLocation = "C:\\management\\management.txt";
    private ChecksumManagement admini;

    public ServerHandler(Socket socket, ClientUI clientui) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        assert (socket.isConnected());
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        admini = new ChecksumManagement(managementLocation);
        this.diskHandler = new DiskHandler(location);
        this.clientui = clientui;
        admini.load();

    }

    public void run() {
        while (!runner.isInterrupted()) {
        }
    }

    public void start() {
        assert (runner == null);
        runner = new Thread(this);
        runner.start();
        System.out.println("Server handler is gestart.");
    }

    public void stop(boolean block) {
        assert (runner != null);
        assert (runner.isAlive());
        runner.interrupt();
        if (block) {
            while (!runner.isInterrupted());
        }
        System.out.println("Server handler is gestopt.");
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

    public void getFileFromServer(String request, String location) throws IOException, ClassNotFoundException {
        
        JSONObject empty = new JSONObject();
        Response response = new Response(empty);
        active = true;
        while (active) {

            try {
                // Send request naar server
                writer.write(request);
                writer.flush();
                active = false;
                System.out.println("GET-Request verstuurd naar Server");

            } catch (IOException e) {
                System.err.println(e.getMessage());
                stop(true);
            }

            try {
                // Verwerk response ontvangen van server    
                response = new Response(empty).unMarshallResponse(reader);
                System.out.println("Response ontvangen : Status " + response.getBody().get("status"));

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
                String filename = response.getBody().get("filename").toString();
                String content = response.getBody().get("content").toString();
                String checksumInResponse = response.getBody().get("checksum").toString();
                
                //  NewFileHandler aanmaken zodat ik object heb
                NewFileHandler fah = new NewFileHandler(filename, checksumInResponse);
                
                // filename niet aanwezig = Opslaan.
                if (admini.fileExistsInList(fah) == false) {
                    admini.addObjectFile(fah);
                    System.out.println("add,, omdat dit de eerste versie van " + fah.getFileName());
                } else {
                    // filename wel aanwezig, dan controle op checksum. Dan gaan we toveren.                   
                    System.out.println("update, omdat we deze al kennen");
                    LocalFileHandler f = diskHandler.getFileHandler(filename);
                    String checksumLocalFile = f.getChecksum();
                    admini.updateFile(fah,checksumLocalFile,checksumInResponse);
                }

                fos = new FileOutputStream(location + filename);
                byte[] buffer = e.decode(content);
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

    public void putFileToServer(String selectedItem) throws IOException {

        JSONObject o = new JSONObject();

        LocalFileHandler f = diskHandler.getFileHandler(selectedItem);

        o.put("filename", selectedItem);
        o.put("checksum", f.getChecksum());
        o.put("original_checksum", f.getOriginalChecksum());

        // En nu nog de inhoud.
        File file = new File(f.getFile().getPath());
        byte[] bytes = loadFile(file);
        byte[] encoded = Base64.getEncoder().encode(bytes);
        String encodedString = new String(encoded);
        o.put("content", encodedString);

        Request r = new Request(Request.Type.PUT, o);
        String request = r.toString();
        writer.write(request);
        writer.flush();
        System.out.println("PUT-Request verstuurd naar Server");

        try {
            // Verwerk response ontvangen van server  
            JSONObject empty = new JSONObject();
            Response response = new Response(empty);
            response = new Response(empty).unMarshallResponse(reader);
            System.out.println("Response ontvangen : Status " + response.getBody().get("status") + "Message :" + response.getBody().get("message"));

        } catch (IOException e) {
            System.err.println(e.getMessage());
            stop(true);
        }

    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

}
