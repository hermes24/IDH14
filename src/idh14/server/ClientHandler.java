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
import java.util.ArrayList;

import org.json.JSONArray;
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
		System.out.println(this.toString() + " is gestart.");
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
		System.out.println(this.toString() + " is gestopt.");
	}

	/**
	 * Implementatie van Runnable.run().
	 */
	public void run() {
		while (!runner.isInterrupted()) {
			try {
				String l = reader.readLine();
				if (!l.isEmpty()) {

					// Bepaal het type request.
					String r = (l.split(" ", 2))[0];
					System.out.println("Request: " + r + " ontvangen op " + this.toString());
					Request.Type t = Request.Type.valueOf(r);
					switch (t) {

					// Een LIST-request heeft verder geen data. Retourneer de
					// lijst met bestanden en hun checksums.
					case LIST:
						Storage s = server.getStorage();
						JSONObject b = new JSONObject();
						JSONArray f = new JSONArray();
						for (String q : s.getFiles()) {
							JSONObject qq = new JSONObject();
							qq.put("filename", q);
							f.put(qq);
						}
						b.put("files", f);
						Response p = new Response(Response.Status.OK, b);
						writer.write(p.toString());
						writer.flush();
						break;

					// Een GET-request bevat de naam van het gewenste bestand.
					case GET:
						break;

					// Een PUT-request bevat de naam van het te plaatsen
					// bestand,
					// de checksum, de oorspronkelijke checksum en de inhoud.
					// Bestaat het bestand nog niet, dan wordt het aangemaakt.
					// Bestaat
					// het al wel, dan wordt de waarde in de meegegeven
					// originele
					// checksum vergeleken met de huidige checksum-waarde op de
					// server. Zijn die gelijk, dan mag het bestand worden
					// overschreven.
					// Zo niet, dan volgt een foutmelding 412.
					case PUT:
						break;

					// Een DELETE-request bevat de naam van het te verwijderen
					// bestand en de checksum.
					case DELETE:
						break;

					}
				}
			} catch (IllegalArgumentException iae) {
				System.err.println("Onbekende request ontvangen op " + this.toString());
			} catch (IOException ioe) {
				System.err.println("Fout bij ontvangen bericht van client op " + this.toString());
			}

		}
	}

	@Override
	public String toString() {
		return "Remote IP-adres/poort " + socket.getRemoteSocketAddress() + '/' + socket.getPort()
				+ " naar lokale poort " + socket.getLocalPort();
	}

}