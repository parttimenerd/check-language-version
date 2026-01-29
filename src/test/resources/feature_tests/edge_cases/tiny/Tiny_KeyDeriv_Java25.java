// Tiny: Key derivation API (Java 25)
// Expected Version: 25
// Required Features: KEY_DERIVATION_API

import javax.crypto.*;

class Tiny_KeyDeriv_Java25 {
    void test() throws Exception {
        KDF kdf = KDF.getInstance("HKDF-SHA256");
    }
}