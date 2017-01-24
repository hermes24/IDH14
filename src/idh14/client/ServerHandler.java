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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
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
        clientui.setMessageBoxText("Verbonden met Server " + socket.getInetAddress() + " @ poort :" + socket.getPort());
    }

    public void stop(boolean block) {
        assert (runner != null);
        assert (runner.isAlive());
        runner.interrupt();
        if (block) {
            while (!runner.isInterrupted());
        }
        System.out.println("Server handler is gestopt.");
        clientui.setMessageBoxText("Verbinding met Server verbroken");
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
                
                clientui.setMessageBoxText("File list opgehaald, Status : " + response.getBody().get("status"));
                
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

                // Request bouwen
                JSONObject o = new JSONObject();
                Request.Type type = Request.Type.GET;

                o.put("filename", Base64Encoded(file));
                Request r = new Request(type, o);
                String request = r.toString();

                // Request versturen
                writer.write(request);
                writer.flush();
                active = false;
                clientui.setMessageBoxText("GET-Request verstuurd naar Server");

            } catch (IOException e) {
                System.err.println(e.getMessage());
                stop(true);
            }

            try {
                // Verwerk response ontvangen van server    
                response = new Response(empty).unMarshallResponse(reader);
                clientui.setMessageBoxText("GET-Response : Status " + response.getBody().get("status"));

            } catch (IOException e) {
                System.err.println(e.getMessage());
                stop(true);
            }

            // Nieuwe file lokaal aanmaken
            FileOutputStream fos = null;

            try {
                // Decode inhoud 
                Base64.Decoder e = Base64.getDecoder();

                // Velden uit response extraheren zodat ik hier verdere acties mee kan uitvoeren.
                String filename64 = response.getBody().get("filename").toString();
                String filename = Base64Decoded(filename64);
                String content = response.getBody().get("content").toString();
                String checksumInResponse = response.getBody().get("checksum").toString();

                //  NewFileHandler aanmaken. Met dit object voer ik het checksum management.
                NewFileHandler fah = new NewFileHandler(filename, checksumInResponse);

                // Administratie bijwerken op basis van zojuist aangemaakte object.
                
                if (admini.addOrUpdate(fah, "get")) {
                    
                    clientui.setMessageBoxText("GET-Response - Checksum administratie bijgewerkt");
                    // Admin geeft TRUE of FALSE terug.
                    // Klopt adminstratie niet doordat een lokale file afwijkend is van server
                    // En er is user interactie vereist. Dan mogen we lokaal niet schrijven.
                    
                    fos = new FileOutputStream(location + filename);
                    byte[] buffer = e.decode(content);
                    fos.write(buffer);
                    
                } else {
                    
                    // False is User om interactie vragen.
                    clientui.clientPopUpMessage("Fout - Lokaal bijwerken niet mogelijk ivm versie verschillen");
                    clientui.setMessageBoxText("GET-Response - Administratie niet bijgewerkt ivm Error");
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

        // Aanmaken benodigdheden om request te vullen.
        JSONObject o = new JSONObject();
        LocalFileWrapper f = diskHandler.getFileWrapper(selectedItem);

        // JSON object vullen met juiste velden.
        o.put("filename", Base64Encoded(selectedItem));
        o.put("checksum", f.getChecksum());
        o.put("original_checksum", admini.getOriginalChecksumFromFile(selectedItem));

        // En nu nog de inhoud.
        File file = new File(f.getFile().getPath());
        byte[] bytes = loadFile(file);
        byte[] encoded = Base64.getEncoder().encode(bytes);
        String encodedString = new String(encoded);
        o.put("content", encodedString);

        // Dit alles nu omtoveren naar request en versturen naar Server.
        Request r = new Request(Request.Type.PUT, o);
        String request = r.toString();
        writer.write(request);
        writer.flush();
        clientui.setMessageBoxText("PUT-Request verstuurd naar Server");

        try {
            // Verwerk het response vanuit de Server
            JSONObject empty = new JSONObject();
            Response response = new Response(empty);
            response = new Response(empty).unMarshallResponse(reader);

            // Afhankelijk van de status moet administratie worden bijgewerkt. 
            // Als file op de server is bijgewerkt, dan moet de client de original_checksum bijwerken in de administratie.
            if (!response.getBody().get("status").toString().contains("412")) {
                NewFileHandler fah = new NewFileHandler(selectedItem, f.getChecksum());
                clientui.setMessageBoxText("PUT-Response - Server heeft file geaccepteerd " + "\n" + "Administatie lokaal bijwerken.");
                if(admini.addOrUpdate(fah, "put")){
                    clientui.setMessageBoxText("PUT-Response - Administratie bijgewerkt !");
                }
            } else {
                clientui.setMessageBoxText("PUT-Response - Server geeft foutcode terug" + "\n" + "Checksum niet bijwerkt in administratie !");
                clientui.clientPopUpMessage("Fout - File updaten aan serverkant niet mogelijk ivm versie verschillen");
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            stop(true);
        }

    }
    
    public void deleteFileFromServer(String selectedItem) throws IOException {

        // Aanmaken benodigdheden om request te vullen.
        JSONObject o = new JSONObject();
        LocalFileWrapper f = diskHandler.getFileWrapper(selectedItem);
        
        // Nog iets toevoegen dat als de file lokaal niet gevonden kan, de gebruiker een melding krijgt.
        
        // JSON object vullen met juiste velden.
        o.put("filename", Base64Encoded(selectedItem));
        o.put("checksum", f.getChecksum());

        // Dit alles nu omtoveren naar request en versturen naar Server.
        Request r = new Request(Request.Type.DELETE, o);
        String request = r.toString();
        System.out.println(r.toString());
        writer.write(request);
        writer.flush();
        clientui.setMessageBoxText("DELETE-Request verstuurd naar Server");

        try {
            // Verwerk het response vanuit de Server
            JSONObject empty = new JSONObject();
            Response response = new Response(empty);
            response = new Response(empty).unMarshallResponse(reader);
            System.out.println(response.toString());

            if (response.getBody().get("status").toString().contains("412")) {
                clientui.setMessageBoxText("DELETE-Response - File niet verwijderd ivm mismatch");

            } else if(response.getBody().get("status").toString().contains("404")){
                clientui.setMessageBoxText("DELETE-Response - Bestand niet gevonden @ server");
            } else {
                clientui.setMessageBoxText("DELETE-Response - Bestand is verwijderd van de server");
                getServerFileList();
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
