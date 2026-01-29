// Tiny: Key encapsulation (Java 21)
// Expected Version: 21
// Required Features: KEY_ENCAPSULATION

import javax.crypto.*;

class Tiny_KEM_Java21 {
    void test() throws Exception {
        KEM kem = KEM.getInstance("DHKEM");
    }
}