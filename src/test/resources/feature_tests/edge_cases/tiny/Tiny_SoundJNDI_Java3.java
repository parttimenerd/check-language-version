// Tricky: Sound + JNDI combo still Java 1.3
// Expected Version: 3
// Required Features: JAVA_SOUND, JNDI
import javax.sound.sampled.Clip;
import javax.naming.Context;

class Tiny_SoundJNDI_Java3 {
    Clip clip;
    Context ctx;
}