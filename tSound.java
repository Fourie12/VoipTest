import java.net.*;
import java.io.*;
import javax.sound.sampled.*;
public class tSound
{
	public static Mixer mixer;
	public static Clip clip;

	public static void main(String[] args)
	{
		Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();
		for (Mixer.Info info : mixInfos) {
			System.out.println(info.getName() + ": " + info.getDescription());
		}
		System.out.println("==================================================");
		int device = 3;
		System.out.println(mixInfos[device].getName() + ": " + mixInfos[device].getDescription());
		mixer = AudioSystem.getMixer(mixInfos[device]);

		DataLine.Info dataInfo = new DataLine.Info(Clip.class, null);
		try {
			clip = (Clip) mixer.getLine(dataInfo);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}

		try {
			URL soundURL = tSound.class.getResource("song.wav");
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
			clip.open(audioStream);
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		} catch (UnsupportedAudioFileException e2) {
			e2.printStackTrace();
		} catch (IOException e3) {
			e3.printStackTrace();
		}

		clip.start();

		do {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (clip.isActive());
	}
}
