// Java 1.0 feature: Applet API
// Expected Version: 0
// Required Features: APPLET
// Compile Check: false
import java.applet.Applet;
import java.applet.AudioClip;

public class Java0_Applet {
    public void playSound(Applet applet) {
        AudioClip clip = applet.getAudioClip(applet.getCodeBase(), "sound.au");
        if (clip != null) {
            clip.play();
        }
    }
}