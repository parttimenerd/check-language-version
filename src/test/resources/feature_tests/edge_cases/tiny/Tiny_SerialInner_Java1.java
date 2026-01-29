// Tricky: Serializable with inner class
// Expected Version: 1
// Required Features: SERIALIZATION, INNER_CLASSES
// Optional Features: IO_API
import java.io.Serializable;

class Tiny_SerialInner_Java1 {
    class Data implements Serializable {}
}