// Java 1.3 feature: Java Sound API
// Expected Version: 3
// Required Features: JAVA_SOUND, IO_API
// Compile Check: skip
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;

class Java3_JavaSound {
    public void playAudio(java.io.File file) throws Exception {
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
    }
}