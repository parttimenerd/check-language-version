### Summary


Adds the Java Sound API in `javax.sound.sampled` and `javax.sound.midi`.

### Details

The Java Sound API provides support for sampled audio and MIDI.

Key packages include:

- [`javax.sound.sampled`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/sound/sampled/package-summary.html) for audio input/output, lines, mixers, and audio formats
- [`javax.sound.midi`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/sound/midi/package-summary.html) for MIDI sequencing and synthesis

Common entry points include [`AudioSystem`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/sound/sampled/AudioSystem.html) (for obtaining mixers/lines and reading audio streams).

### Example

```java
// Open an audio input stream from a file (illustrative)
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

class Example {
    AudioInputStream demo(File f) throws Exception {
        return AudioSystem.getAudioInputStream(f); // decode audio file
    }
}
```

### Historical

Introduced in Java 1.3 to provide built-in audio and MIDI capabilities in the standard desktop APIs.

### Links

- [Package javax.sound.sampled (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/sound/sampled/package-summary.html)
- [AudioSystem (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/sound/sampled/AudioSystem.html)
- [Package javax.sound.midi (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/sound/midi/package-summary.html)