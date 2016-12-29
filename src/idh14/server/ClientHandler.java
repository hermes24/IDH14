package idh14.server;

//File Sharing
//
//Luc Hermes  |  Eric Marsilje  |  Joost van Stuijvenberg
//
//Avans Hogeschool Breda - IDH14
//November/December 2016

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.json.JSONObject;

import idh14.protocol.Request;
import idh14.protocol.Response;

/**
 * Een ClientHandler-instantie bedient één (1) remote client.
 */
public class ClientHandler implements Runnable {
	// TODO: Log4J

	/**
	 * TCP-socketverbinding met de remote client.
	 */
	private final Socket socket;

	/**
	 * BufferedReader voor het lezen van de client requests.
	 */
	private final BufferedReader reader;

	/**
	 * BufferedWriter voor het schrijven van de server responses.
	 */
	private final BufferedWriter writer;

	/**
	 * Server instance.
	 */
	private final Server server;

	/**
	 * Thread die deze instantie van een ClientHandler bedient.
	 */
	private Thread runner;

	/**
	 * ClientHandler.
	 * 
	 * @param socket
	 *            lokale TCP-socket waarlangs de verbinding met de remote client
	 *            verloopt.
	 */
	public ClientHandler(Socket socket, Server server) throws IOException {
		assert (socket.isConnected());
		this.socket = socket;
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		this.server = server;
	}

	/**
	 * Start de thread die deze ClientHandler-instantie bedient.
	 */
	public void start() {
		assert (runner == null);
		runner = new Thread(this);
		runner.start();
		System.out.println("Client handler voor " + toString() + " is gestart.");
	}

	/**
	 * Stopt de thread die deze ClientHandler-instantie bedient.
	 * 
	 * @param block
	 *            wel of niet wachten tot er daadwerkelijk gestopt is
	 */
	public void stop(boolean block) {
		assert (runner != null);
		assert (runner.isAlive());
		runner.interrupt();
		if (block)
			while (!runner.isInterrupted())
				;
		System.out.println("Client handler voor " + toString() + " is gestopt.");
	}

	/**
	 * Handle LIST-request.
	 */
	private void handleListRequest() throws IOException {
		Storage s = server.getStorage();
		JSONObject b = new JSONObject();
		b.put("status", Response.Status.OK.getCode());

		if (!s.getFileWrappers().isEmpty())
			for (FileWrapper w : s.getFileWrappers()) {
				JSONObject o = new JSONObject();
				o.put("filename", w.getFile().getName());
				o.put("checksum", w.getChecksum());
				b.append("files", o);
			}

		Response p = new Response(b);
		p.marshall(writer);
	}

	/**
	 * Implementatie van Runnable.run().
	 */
	public void run() {
		while (!runner.isInterrupted()) {
			try {
				Request r = Request.unMarshallRequest(reader);
				if (r == null) {
					System.out.println("Client op " + this.toString()
							+ " heeft verbinding verbroken. Client handler wordt gestopt.");
					stop(true);
				} else {
					System.out.println(r.getType() + "-request ontvangen op " + this.toString());
					switch (r.getType()) {
					case LIST:
						handleListRequest();
						break;
					case GET:
						break;
					case PUT:
						break;
					case DELETE:
						break;
					}
				}
			} catch (IOException ioe) {
				System.err.println(
						"Fout bij ontvangen bericht van client op " + this.toString() + ". Fout: " + ioe.getMessage());
				stop(true);
			}
		}
	}

	@Override
	public String toString() {
		return "remote IP-adres/poort " + socket.getRemoteSocketAddress() + '/' + socket.getPort()
				+ " naar lokale poort " + socket.getLocalPort();
	}

}