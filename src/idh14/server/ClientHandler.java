package idh14.server;

//File Sharing
//
//Luc Hermes  |  Eric Marsilje  |  Joost van Stuijvenberg
//
//Avans Hogeschool Breda - IDH14
//November/December 2016

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import idh14.protocol.Request;
import idh14.protocol.Response;

/**
 * Een ClientHandler-instantie bedient één remote client.
 */
public class ClientHandler implements Runnable {

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
		try {
			Storage s = server.getStorage();
			JSONObject b = new JSONObject();
			b.put("status", Response.Status.OK.getCode());
			if (!s.getFileWrappers().isEmpty())
				for (FileWrapper w : s.getFileWrappers()) {
					JSONObject o = new JSONObject();
					o.put("filename", Base64Encoded(w.getFile().getName()));
					o.put("checksum", w.getChecksum());
					b.append("files", o);
				}
			new Response(b).marshall(writer);
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception e) {
			JSONObject j = new JSONObject();
			j.put("status", Response.Status.GEEN_IDEE);
			new Response(j).marshall(writer);
		}
	}

	/**
	 * Handle GET-request.
	 */
	private void handleGetRequest(Request request) throws IOException {
		try {
			Storage s = server.getStorage();
			JSONObject b = new JSONObject();
			String filename = null;
			try {
				filename = Base64Decoded(request.getBody().getString("filename"));
			} catch (JSONException je) {
				b.put("status", Response.Status.GEEN_IDEE.getCode());
				new Response(b).marshall(writer);
				return;
			}

			FileWrapper f = null;
			for (FileWrapper w : s.getFileWrappers())
				if (w.getFile().getName().equals(filename)) {
					f = w;
					break;
				}
			if (f == null) {
				b.put("status", Response.Status.NOT_FOUND.getCode());
				new Response(b).marshall(writer);
				return;
			}

			b.put("status", Response.Status.OK.getCode());
			b.put("filename", Base64Encoded(f.getFile().getName()));
			b.put("checksum", f.getChecksum());

			// En nu nog de inhoud.
			File file = new File(f.getFile().getPath());
			byte[] bytes = loadFile(file);
			byte[] encoded = Base64.getEncoder().encode(bytes);
			String encodedString = new String(encoded);
			b.put("content", encodedString);

			new Response(b).marshall(writer);
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception e) {
			JSONObject j = new JSONObject();
			j.put("status", Response.Status.GEEN_IDEE);
			new Response(j).marshall(writer);
		}
	}

	/**
	 * Handle PUT-request.
	 */
	private void handlePutRequest(Request request) throws IOException {
		try {
			Storage s = server.getStorage();
			JSONObject b = new JSONObject();
			String f = Base64Decoded(request.getBody().getString("filename"));

			// Als het bestand al bestaat, controleer of de meegegeven originele
			// checksum van het
			// bestand overeenkomt met dat op de server. Zo nee: foutcode 412.
			if (s.fileExists(f)) {
				FileWrapper w = s.getFileWrapper(f);
				String c1 = request.getBody().getString("original_checksum");
				String c2 = w.getChecksum();
				if (!c1.equals(c2)) {
					b.put("status", Response.Status.CONFLICT.getCode());
					new Response(b).marshall(writer);
					return;
				}
			}

			// En nu gaan we schrijven.
			byte[] buf = Base64.getDecoder().decode(request.getBody().getString("content"));
			FileOutputStream fos = s.getOutputStream(f);
			fos.write(buf);
			b.put("status", Response.Status.OK);
			new Response(b).marshall(writer);
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception e) {
			JSONObject j = new JSONObject();
			j.put("status", Response.Status.GEEN_IDEE);
			new Response(j).marshall(writer);
		}
	}

	/**
	 * Handle DELETE-request.
	 */
	private void handleDeleteRequest(Request request) throws IOException {
		try {
			Storage s = server.getStorage();
			JSONObject b = new JSONObject();
			String f = Base64Decoded(request.getBody().getString("filename"));

			// Bestand moet wel bestaan natuurlijk.
			if (!s.fileExists(f)) {
				b.put("status", Response.Status.NOT_FOUND.getCode());
				b.put("message", "Bestand niet gevonden.");
			} else {

				// En de checksums moeten ook overeenkomen.
				FileWrapper w = s.getFileWrapper(f);
				String c1 = request.getBody().getString("checksum");
				String c2 = w.getChecksum();
				if (!c1.equals(c2)) {
					b.put("status", Response.Status.CONFLICT.getCode());
					b.put("message", "Bestand bestaat, maar met een andere checksum. Bekijk het maar met je DELETE.");
				} else {
					s.deleteFile(f);
					b.put("status", Response.Status.OK.getCode());
					b.put("message", "Bestand is verwijderd.");
				}
			}

			new Response(b).marshall(writer);
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception e) {
			JSONObject j = new JSONObject();
			j.put("status", Response.Status.GEEN_IDEE);
			new Response(j).marshall(writer);
		}
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
						handleGetRequest(r);
						break;
					case PUT:
						handlePutRequest(r);
						break;
					case DELETE:
						handleDeleteRequest(r);
						break;
					}
				}
			} catch (IOException ioe) {
				System.err.println(
						"Fout bij ontvangen bericht van client op " + this.toString() + ". Fout: " + ioe.getMessage());
				try {
					JSONObject j = new JSONObject();
					j.put("status", Response.Status.ERROR);
					new Response(j).marshall(writer);
				} catch (IOException ioe2) {
				}

				stop(true);
			}
		}
	}

	@Override
	public String toString() {
		return "remote IP-adres/poort " + socket.getRemoteSocketAddress() + '/' + socket.getPort()
				+ " naar lokale poort " + socket.getLocalPort();
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

	/**
	 * Utility method voor decodering uit Base64.
	 */

	private static final String Base64Decoded(String source) {
		return new String(Base64.getDecoder().decode(source));
	}
}