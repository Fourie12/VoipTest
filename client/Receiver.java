package client;

import java.io.*;
import java.net.*;

/**
 * Server class that receives a WAV file over TCP connection
 */
public class Receiver {
	private static final int BUFFER_SIZE = 8096;
	private int port;
	private String saveDirectory;

	public Receiver(int port, String saveDirectory) {
		this.port = port;
		this.saveDirectory = saveDirectory;

		// Create save directory if it doesn't exist
		File directory = new File(saveDirectory);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	public void start() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("Receiver started on port " + port);
			System.out.println("Waiting for connection...");

			try (Socket clientSocket = serverSocket.accept()) {
				System.out.println("Connection accepted from: " + clientSocket.getInetAddress());
				receiveFile(clientSocket);
			} catch (IOException e) {
				System.err.println("Error handling client connection: " + e.getMessage());
			}
		} catch (IOException e) {
			System.err.println("Could not start server: " + e.getMessage());
		}
	}

	private void receiveFile(Socket socket) throws IOException {
		try (
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())
		) {
			// Read the filename
			String filename = dataInputStream.readUTF();
			System.out.println("Receiving file: " + filename);

			// Read the file size
			long fileSize = dataInputStream.readLong();
			System.out.println("File size: " + fileSize + " bytes");

			// Create the file output stream
			String filePath = saveDirectory + File.separator + filename;
			try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesRead;
				long totalBytesRead = 0;
				long startTime = System.currentTimeMillis();

				// Read the file data and write to the output file
				while (totalBytesRead < fileSize && (bytesRead = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalBytesRead))) != -1) {
					fileOutputStream.write(buffer, 0, bytesRead);
					totalBytesRead += bytesRead;

					// Print progress every 5%
					if (totalBytesRead % (fileSize / 20) < BUFFER_SIZE) {
						int progress = (int) ((totalBytesRead * 100) / fileSize);
						System.out.print("\rReceiving: " + progress + "% complete");
					}
				}

				long endTime = System.currentTimeMillis();
				System.out.println("\nFile received successfully: " + filePath);
				System.out.println("Transfer time: " + (endTime - startTime) + " ms");
			}
		}
	}

	public static void main(String[] args) {
		int port = 8080;
		String saveDirectory = "received_files";

		// Allow command line arguments to override defaults
		if (args.length >= 1) {
			port = Integer.parseInt(args[0]);
		}
		if (args.length >= 2) {
			saveDirectory = args[1];
		}

		Receiver receiver = new Receiver(port, saveDirectory);
		receiver.start();
	}
}
