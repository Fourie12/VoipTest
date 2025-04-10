package client;

import client.WavPlayer;

public class VoiceNote {
	public static void main(String[] args) {
		String filename = args[0];
		System.out.println(filename);
		WavPlayer vn = new WavPlayer(filename);
	}
}
