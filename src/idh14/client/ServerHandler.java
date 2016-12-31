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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
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

    public void getServerFileList() {

        active = true;

        while (active) {
            JSONObject b = new JSONObject();
            Request.Type type = Request.Type.LIST;
            Request r = new Request(type, b);
            String request = r.toString();

            try {
                writer.write(request);
                writer.flush();
                active = false;

            } catch (IOException e) {
                System.err.println(e.getMessage());
                stop(true);
            }

            try {
                JSONObject b2 = new JSONObject();
                Response rs = new Response(b2);
                rs.unMarshallResponse(reader);

            } catch (IOException e) {
                System.err.println(e.getMessage());
                stop(true);
            }

        }
    }

}
