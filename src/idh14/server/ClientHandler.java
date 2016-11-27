package idh14.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

// File Sharing
//
// Luc Hermes  |  Eric Marsilje  |  Joost van Stuijvenberg
//
// Avans Hogeschool Breda - IDH14
// November/December 2016

import java.net.Socket;

import idh14.protocol.File;
import idh14.protocol.ListResponse;
import idh14.protocol.RequestType;

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
	 * Folder.
	 */
	private final Folder folder;

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
	public ClientHandler(Socket socket, Folder folder) throws IOException {
		assert (socket.isConnected());
		this.socket = socket;
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		this.folder = folder;
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
					String r = (l.split(" ", 2))[0];
					System.out.println("Request: " + r + " ontvangen op " + this.toString());
					RequestType t = RequestType.valueOf(r);
					switch (t) {
					
					// Een LIST-request heeft verder geen data.
					case LIST:
						ListResponse lr = new ListResponse(200);
						File f = new File();
						f.setFilename("Bladiebla.pdf");
						f.setChecksum("8578201cf22b83bdaef44e1c5a5dc2e764218aa8");
						lr.addFile(f);
						writer.write(lr.toString());
						writer.flush();
						break;
					// Een GET-request bevat de naam van het gewenste bestand.
					case GET:
						break;
					// Een PUT-request bevat de naam van het te plaatsen bestand,
					// de checksum, de oorspronkelijke checksum en de inhoud.
					case PUT:
						break;
					// Een DELETE-request bevat de naam van het te verwijderen
					// bestand en de checksum.
					case DELETE:
						break;
						
					}
				}
			} catch (IllegalArgumentException iae) {
				System.err.println("Onbekend request ontvangen op " + this.toString());
			} catch (IOException ioe) {
				System.err.println("Fout bij ontvangen van client op " + this.toString());
			}

		}
	}

	@Override
	public String toString() {
		return "Remote IP-adres/poort " + socket.getRemoteSocketAddress() + '/' + socket.getPort()
				+ " naar lokale poort " + socket.getLocalPort();
	}

}