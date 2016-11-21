package idh14.server;

import java.net.Socket;

public class ClientHandler implements Runnable {

	private final Socket socket;
	
	private final Thread runner;
	
	public ClientHandler(Socket socket) {
		assert(socket.isConnected());
		this.socket = socket;
		this.runner = new Thread(this);
	}
	
	public void run() {
		
	}
	
}