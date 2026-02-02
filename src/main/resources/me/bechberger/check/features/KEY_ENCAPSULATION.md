### Summary


Adds Key Encapsulation Mechanism (KEM) support via [`javax.crypto.KEM`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/crypto/KEM.html).

### Details

A KEM (Key Encapsulation Mechanism) is a cryptographic primitive used to establish a shared secret.

High level flow:

- the receiver generates a key pair
- the sender uses the receiver's public key to *encapsulate* a secret, producing encapsulated key material + a shared secret
- the receiver *decapsulates* the encapsulated material with the private key to obtain the same shared secret

Java exposes this via [`KEM`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/crypto/KEM.html), returning [`KEM.Encapsulator`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/crypto/KEM.Encapsulator.html) and [`KEM.Decapsulator`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/crypto/KEM.Decapsulator.html) objects.

### Example

```java
// Perform a KEM key agreement (illustrative; depends on available algorithms/providers)
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import javax.crypto.KEM;

class Example {
    byte[] demo() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("X25519"); // example key pair
        KeyPair kp = kpg.generateKeyPair();

        KEM kem = KEM.getInstance("DHKEM"); // illustrative algorithm name
        KEM.Encapsulator enc = kem.newEncapsulator(kp.getPublic());
        KEM.Encapsulated e = enc.encapsulate();

        KEM.Decapsulator dec = kem.newDecapsulator(kp.getPrivate());
        return dec.decapsulate(e.encapsulation()); // shared secret
    }
}
```

### Historical

Introduced in Java 21 via JEP 452 as part of improving Java's cryptography APIs for modern protocols.

### Links

- [KEM (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/crypto/KEM.html)
- [JEP 452: Key Encapsulation Mechanism API](https://openjdk.org/jeps/452)
- [Package javax.crypto (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/crypto/package-summary.html)