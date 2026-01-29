// Test: JavaSound API (Java 1.3)
// Expected Version: 3
// Required Features: JAVA_SOUND, IO_API
import javax.sound.sampled.AudioSystem;
import java.io.File;

class Tiny_JavaSound_Java3 {
    void play() throws Exception {
        AudioSystem.getAudioInputStream(new File("test.wav"));
    }
}