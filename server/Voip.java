package server;

import javax.sound.sampled.*;
import java.net.*;

public class Voip {
	public Voip(String ip) {
		try {
			int port = 50005;
			DatagramSocket socket = new DatagramSocket(port);

			AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, false);
		    DataLine.Info infoOut = new DataLine.Info(SourceDataLine.class, format);

			SourceDataLine speaker = (SourceDataLine) AudioSystem.getLine(infoOut);
			speaker.open(format);
			speaker.start();

		    DataLine.Info infoIn = new DataLine.Info(TargetDataLine.class, format);
		    TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(infoIn);
		    microphone.open(format);
			microphone.start();

			InetAddress clientAddress = InetAddress.getByName(ip);
			int clientPort = 50006;

			Thread receiveThread = new Thread(() -> {
				try {
					byte[] buffer = new byte[4096];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				    while (true) {
						socket.receive(packet);
						speaker.write(packet.getData(), 0, packet.getLength());
					}
				} catch (Exception e) {
					e.printStackTrace();
			    }
			});

			Thread sendThread = new Thread(() -> {
				try {
					byte[] buffer = new byte[4096];
					while (true) {
						microphone.read(buffer, 0, buffer.length);
						DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
						socket.send(packet);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			receiveThread.start();
		    sendThread.start();
		} catch (LineUnavailableException lue) {
			lue.printStackTrace();
		} catch (UnknownHostException uhe) {
			uhe.printStackTrace();
		} catch (SocketException se) {
			se.printStackTrace();
		}
    }
}
