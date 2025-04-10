package client;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class WavPlayer {

	public WavPlayer(String filename) {
        try {
            // Load the audio file
            File soundFile = new File(filename);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

            // Get a clip resource and open it
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Start playback
            clip.start();

            // Keep the program alive while audio is playing
            do {
				Thread.sleep(50);
            } while (clip.isRunning());

            clip.close();
            audioStream.close();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

