// Tricky: RMI with inner class
// Expected Version: 1
// Required Features: RMI, INNER_CLASSES
import java.rmi.Remote;

class Tiny_RMIInner_Java1 {
    class Handler implements Remote {}
}