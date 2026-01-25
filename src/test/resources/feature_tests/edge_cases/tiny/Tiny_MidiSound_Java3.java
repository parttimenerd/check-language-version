// Tricky: MIDI looks modern but is Java 1.3
// Expected Version: 3
// Required Features: JAVA_SOUND
import javax.sound.midi.MidiSystem;
public class Tiny_MidiSound_Java3 {
    void getMidi() throws Exception {
        MidiSystem.getSynthesizer();
    }
}