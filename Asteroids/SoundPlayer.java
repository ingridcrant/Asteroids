import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer {
	public static final String fire = "file:./sounds/fire.wav";
	public static final String thrust = "file:./sounds/thrust.wav";
	public static final String smallExplosion = "file:./sounds/bangSmall.wav";
	public static final String mediumExplosion = "file:./sounds/bangMedium.wav";
	public static final String largeExplosion = "file:./sounds/bangLarge.wav";
	public static final String beat1 = "file:./sounds/beat1.wav";
	public static final String beat2 = "file:./sounds/beat2.wav";

	// used to play sound effects
	// soundToPlay is a string specifying the relative path of the sound effect file

	public static void playSoundEffect(String soundToPlay) {
		URL soundLocation;
		try {
			soundLocation = new URL(soundToPlay);
			Clip clip = null;
			clip = AudioSystem.getClip();
			AudioInputStream inputStream;
			inputStream = AudioSystem.getAudioInputStream(soundLocation);
			clip.open(inputStream);
			clip.loop(0);														// sound does not loop
			clip.start();														// play sound
			
			clip.addLineListener(new LineListener() {							// kill sound thread
				public void update (LineEvent evt) {
					if (evt.getType() == LineEvent.Type.STOP) {
						evt.getLine().close();
					}
				}
			});
			
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			System.out.println(e.getMessage());
		}
	}

}