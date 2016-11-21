package idh14.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

	public static final int PORT = 54321;

	public Server(int port) throws IOException {
		ServerSocket s = new ServerSocket(port);
	}

	public static void main(String[] args) {

		try {

			// TODO: poortnummer van de command line kunnen ontvangen.
			new Server(PORT);

		} catch (Exception e) {
			System.err.println("Er is iets misgegaan bij het opstarten van de server:");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
	}

}
