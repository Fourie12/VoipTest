package client;

import java.io.*;
import java.net.*;

public class ReceiverTCP {

    public static boolean receiveTCP(String myIP) {
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port, 1, InetAddress.getByName(myIP))) {
            System.out.println("Waiting for sender...");

            Socket socket = serverSocket.accept();
            System.out.println("Sender connected.");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            InputStream in = socket.getInputStream();

            // Read metadata
            String filename = reader.readLine();
            long fileSize = Long.parseLong(reader.readLine());

            File outputFile = new File("received_" + filename);
            FileOutputStream fileOut = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalReceived = 0;

            while ((bytesRead = in.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
                totalReceived += bytesRead;
                if (totalReceived >= fileSize) break;
            }

            fileOut.close();
            System.out.println("File received: " + outputFile.getName());
            return true;

        } catch (IOException e) {
            System.err.println("Error in receiveTCP: " + e.getMessage());
            return false;
        }
    }

    // For testing
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ReceiverTCP <your IP>");
            return;
        }
        receiveTCP(args[0]);
    }
}

