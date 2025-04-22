package client;

import java.io.*;
import java.net.*;

public class SenderTCP {

    public static boolean transferTCP(String receiverIP, String filename) {
        int port = 5000;
        File fileToSend = new File(filename); // <-- Change to your .wav file

        final int retryDurationMillis = 20000; // total retry time
        final int retryIntervalMillis = 250;  // wait time between attempts
        long startTime = System.currentTimeMillis();

        Socket socket = null;

        // Try connecting with retry
        while (socket == null && (System.currentTimeMillis() - startTime) < retryDurationMillis) {
            try {
                socket = new Socket(receiverIP, port);
            } catch (IOException e) {
                try {
                    Thread.sleep(retryIntervalMillis);
                } catch (InterruptedException ignored) {}
            }
        }

        if (socket == null) {
            System.err.println("Failed to connect to receiver after retrying.");
            return false;
        }

        try (
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            FileInputStream fileIn = new FileInputStream(fileToSend);
            OutputStream out = socket.getOutputStream()
        ) {
            System.out.println("Connected to receiver.");

            // Send metadata
            writer.println(fileToSend.getName());
            writer.println(fileToSend.length());

            // Send file content
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileIn.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.flush();
            System.out.println("File sent successfully.");
            return true;

        } catch (IOException e) {
            System.err.println("Error during file transfer: " + e.getMessage());
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    // For testing
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java SenderTCP <receiver IP> <filename>");
            return;
        }
        transferTCP(args[0], args[1]);
    }
}

