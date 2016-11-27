package idh14.server;

import java.io.BufferedReader;

// File Sharing
//
// Luc Hermes  |  Eric Marsilje  |  Joost van Stuijvenberg
//
// Avans Hogeschool Breda - IDH14
// November/December 2016

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * De Server-klasse bevat de serverapplicatie voor het file sharing protocol.
 */
public class Server implements Runnable {
	// TODO: Log4J

	/**
	 * Default port is 54321. Gebruikt indien geen poortnummer is opgegeven in
	 * de configuratie of via de command line.
	 */
	public static final int PORT = 54321;

	/**
	 * Server socket.
	 */
	private final ServerSocket socket;

	/**
	 * Server thread.
	 */
	private final Thread thread;

	/**
	 * Folder.
	 */
	private final Folder folder;

	/**
	 * Lijst van verbonden remote clients.
	 */
	private List<ClientHandler> clientHandlers = new ArrayList<>();

	/**
	 * Server.
	 * 
	 * @param port
	 *            poortnummer
	 * @param folderPath
	 *            te synchroniseren bestandsmap
	 */
	public Server(int port, String folderPath) throws IOException {
		socket = new ServerSocket(port);
		folder = new Folder(folderPath);
		thread = new Thread(this);
	}

	@Override
	public void run() {
		try {

			// Voor iedere inkomende TCP-verbinding wordt een ClientHandler-
			// instantie aangemaakt en gestart. Deze krijgt naast de lokale
			// socket ook een referentie naar de Folder-instantie mee.
			System.out.println("Server wacht op binnenkomende connecties op poort " + socket.getLocalPort() + '.');
			while (!Thread.currentThread().isInterrupted()) {
				Socket c = socket.accept();
				ClientHandler h = new ClientHandler(c, folder);
				h.start();
				clientHandlers.add(h);
				System.out.println("TCP-connectie ontvangen: " + h.toString());

			}
		} catch (Exception e) {
			System.err.println("Fout opgetreden in server thread: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException ioe) {
				// Eventuele IOExceptions bij het sluiten van de socket worden
				// genegeerd.
			}
			System.out.println("Server socket gesloten.");
		}
		System.out.println("Server is afgesloten.");
	}

	public void start() {
		thread.start();
	}

	/**
	 * Stop alle client handlers en beëindig deze server-instantie.
	 */
	public void stop() {
		System.out.println("Client handlers worden gestopt.");
		for (ClientHandler h : clientHandlers) {
			h.stop(true);
		}
		System.out.println("Alle client handlers zijn gestopt.");
		thread.interrupt();
	}
	
	/**
	 * Entry point van de serverapplicatie.
	 * 
	 * @param args
	 *            command line parameters
	 */
	public static void main(String[] args) {

		// TODO: poortnummer uit configuratie
		// TODO: poortnummer van de command line kunnen ontvangen.
		// TODO: foldernaam idem
		try {
			Server s = new Server(PORT, "/Users/Joost/");
			s.start();

			// Vanaf hier draait de server in zijn eigen thread. De main thread
			// gebruiken we om een minimalistische beheer-interface mee te faciliteren.
			String c;
			do {
				BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Commando's: C(lients), I(nfo), Q(uit)");
				c = b.readLine();
				switch (c) {
				case "c":
				case "C":
					for (ClientHandler h : s.clientHandlers) {
						System.out.println(h.toString());
					}
					break;
				case "i":
				case "I":
					// TODO: nog iets nuttigs implementeren. Overzicht gebruikt geheugen of
					// iets degelijks.
					break;
				}
			} while (!c.equalsIgnoreCase("q"));
			s.stop();
			System.exit(0);
		} catch (IOException ioe) {
			System.err.println("Fout opgetreden bij aanmaken van de server-instantie: " + ioe.getMessage());
			ioe.printStackTrace();
		}

	}

}
