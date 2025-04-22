package client;

import java.io.*;
import java.net.*;

/**
 * Client class that sends a WAV file over TCP connection
 */
public class Sender {
	private static final int BUFFER_SIZE = 8096;
	private String serverAddress;
	private int port;

	public Sender(String serverAddress, int port) {
		this.serverAddress = serverAddress;
		this.port = port;
	}

	public void sendFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			System.err.println("File does not exist or is not a regular file: " + filePath);
			return;
		}

		// Check if it's a WAV file
		if (!filePath.toLowerCase().endsWith(".wav")) {
			System.err.println("Warning: File does not have .wav extension. Proceeding anyway.");
		}

		Socket socket = new Socket();
		// wait for about 10 seconds retrying every few miliseconds
		for (int i = 0; i < 200; i++) {
			try {
				socket = new Socket(serverAddress, port);
				System.out.println("Connected to receiver: " + serverAddress + ":" + port);
				transferFile(socket, file);
				break;
			} catch (IOException e) {
				// ignore
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		if (!socket.isConnected()) {
			System.out.println("Could not connect to receiver");
		}
	}

	private void transferFile(Socket socket, File file) throws IOException {
		try (
			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
			FileInputStream fileInputStream = new FileInputStream(file)
		) {
			// Send the filename
			dataOutputStream.writeUTF(file.getName());

			// Send the file size
			long fileSize = file.length();
			dataOutputStream.writeLong(fileSize);

			// Send the file data
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead;
			long totalBytesSent = 0;
			long startTime = System.currentTimeMillis();

			while ((bytesRead = fileInputStream.read(buffer)) != -1) {
				dataOutputStream.write(buffer, 0, bytesRead);
				totalBytesSent += bytesRead;

				// Print progress every 5%
				if (totalBytesSent % (fileSize / 20) < BUFFER_SIZE) {
					int progress = (int) ((totalBytesSent * 100) / fileSize);
					System.out.print("\rSending: " + progress + "% complete");
				}
			}

			// Flush to ensure all data is sent
			dataOutputStream.flush();

			long endTime = System.currentTimeMillis();
			System.out.println("\nFile sent successfully: " + file.getName());
			System.out.println("File size: " + fileSize + " bytes");
			System.out.println("Transfer time: " + (endTime - startTime) + " ms");
		}
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: java Sender <file-path> [server-address] [port]");
			return;
		}

		String filePath = args[0];
		String serverAddress = "localhost";
		int port = 8080;

		// Allow command line arguments to override defaults
		if (args.length >= 2) {
			serverAddress = args[1];
		}
		if (args.length >= 3) {
			port = Integer.parseInt(args[2]);
		}

		Sender sender = new Sender(serverAddress, port);
		sender.sendFile(filePath);
	}
}
