package client;

import javax.sound.sampled.*;
import java.net.*;

public class Voip {
	private DatagramSocket socket;
	private SourceDataLine speaker;
	private TargetDataLine microphone;
	private Thread sendThread, receiveThread;
	private volatile boolean running = true;

	public Voip(String ip, int port1, int port2) {
		try {
			int port = port1;
			socket = new DatagramSocket(port);

			AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, false);

			DataLine.Info infoOut = new DataLine.Info(SourceDataLine.class, format);
			speaker = (SourceDataLine) AudioSystem.getLine(infoOut);
			speaker.open(format);
			speaker.start();

			DataLine.Info infoIn = new DataLine.Info(TargetDataLine.class, format);
			microphone = (TargetDataLine) AudioSystem.getLine(infoIn);
			microphone.open(format);
			microphone.start();

			InetAddress clientAddress = InetAddress.getByName(ip);
			int clientPort = port2;

			receiveThread = new Thread(() -> {
				try {
					byte[] buffer = new byte[4096];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					while (running) {
						socket.receive(packet);
						speaker.write(packet.getData(), 0, packet.getLength());
					}
				} catch (Exception e) {
					if (running) e.printStackTrace(); // Ignore if stopping
				}
			});

			sendThread = new Thread(() -> {
				try {
					byte[] buffer = new byte[4096];
					while (running) {
						microphone.read(buffer, 0, buffer.length);
						DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
						socket.send(packet);
					}
				} catch (Exception e) {
					if (running) e.printStackTrace();
				}
			});

			receiveThread.start();
			sendThread.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		running = false;

		if (microphone != null) {
			microphone.stop();
			microphone.close();
		}
		if (speaker != null) {
			speaker.stop();
			speaker.close();
		}
		if (socket != null && !socket.isClosed()) {
			socket.close();
		}

		System.out.println("VoIP stopped.");
	}
}

