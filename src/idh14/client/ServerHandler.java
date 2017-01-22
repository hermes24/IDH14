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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import static java.nio.file.Files.list;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import static java.util.Collections.list;
import java.util.List;
import org.json.JSONArray;
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
        this.diskHandler = new DiskHandler(location);
        admini = new ChecksumManagement(managementLocation, diskHandler);

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

    public ArrayList<String> getServerFileList() {
        
        ArrayList<String> listServerFiles = new ArrayList<String>();

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
                JSONArray list = new JSONArray();
                list = response.getBody().getJSONArray("files");

            for (int i = 0; i < list.length(); i++) {
                JSONObject o = list.getJSONObject(i);
                
                listServerFiles.add(Base64Decoded(o.getString("filename")));
            }
                
                

            } catch (IOException e) {
                System.err.println(e.getMessage());
                stop(true);
            }
        }

        return listServerFiles;

    }

    public void getFileFromServer(String file) throws IOException, ClassNotFoundException {
        
        JSONObject empty = new JSONObject();
        Response response = new Response(empty);
        active = true;
        while (active) {

            try {

                JSONObject o = new JSONObject();
                Request.Type type = Request.Type.GET;

                o.put("filename", Base64Encoded(file));
                Request r = new Request(type, o);
                String request = r.toString();

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
                String filename64 = response.getBody().get("filename").toString();
                String filename = Base64Decoded(filename64);
                String content = response.getBody().get("content").toString();
                String checksumInResponse = response.getBody().get("checksum").toString();
                
                //  NewFileHandler aanmaken zodat ik object heb
                NewFileHandler fah = new NewFileHandler(filename, checksumInResponse);
                System.out.println("checksum vanuit server voor deze file : " + checksumInResponse);
                
                // Administratie bijwerken 
                if (admini.addOrUpdate(fah,"get")) {

                    // Klopt adminstratie niet doordat een lokale file afwijkend van server
                    // En er is user interactie vereist. Dan mogen we lokaal niet schrijven.
                    
                    fos = new FileOutputStream(location + filename);
                    byte[] buffer = e.decode(content);
                    fos.write(buffer);
                }

            } catch (IOException e) {
                e.getMessage();
            } finally {
                if (fos != null) {
                    fos.close();

                }
            }
        }
    }

    public void putFileToServer(String selectedItem) throws IOException, FileNotFoundException, ClassNotFoundException {

        JSONObject o = new JSONObject();

        LocalFileWrapper f = diskHandler.getFileWrapper(selectedItem);

        o.put("filename", Base64Encoded(selectedItem));
        o.put("checksum", f.getChecksum());
        System.out.println("Filename waar ik checksum van ga opvragen : " + selectedItem);
        o.put("original_checksum", admini.getOriginalChecksumFromFile(selectedItem));


        // En nu nog de inhoud.
        File file = new File(f.getFile().getPath());
        byte[] bytes = loadFile(file);
        byte[] encoded = Base64.getEncoder().encode(bytes);
        String encodedString = new String(encoded);
        o.put("content", encodedString);
        System.out.println(o.toString());

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
            System.out.println("Response ontvangen : Status " + response.getBody().get("status"));
            
            if(!response.getBody().get("status").toString().contains("412")){
                NewFileHandler fah = new NewFileHandler(selectedItem, f.getChecksum());
                System.out.println("Gaan file updaten aan server kant ,, dan ook lokaal checksum bijwerken.");
                admini.addOrUpdate(fah,"put");
            } else {
                System.out.println("Server geeft foutcode terug. Checksum niet bijwerkt in administratie");
            }

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
    
	/**
	 * Utility method voor decodering uit Base64.
	 */

	private static final String Base64Decoded(String source) {
		return new String(Base64.getDecoder().decode(source));
	}
        
            	/**
	 * Utility method voor codering naar Base64.
	 */
	private static final String Base64Encoded(String source) {
		String result = null;
		try {
			byte[] b = source.getBytes("UTF-8");
			result = new String(Base64.getEncoder().encode(b));
		} catch (UnsupportedEncodingException uce) {
			result = "Encoding to Base64 failed.";
		}
		return result;
	}

}
