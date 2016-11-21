package idh14.client;

import java.io.IOException;
import java.net.Socket;

public class Client {

	public static final String SERVER = "localhost";
	public static final int PORT = 54321;
	
	private Socket socket;
	
	public Client(String server, int port) throws IOException {
		socket = new Socket(server , port);
	}

	public static void main(String[] args) {

		try {
			new Client(SERVER, PORT);

		} catch (Exception e) {
			System.err.println("Er is iets misgegaan bij het opstarten van de client:");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}

}
