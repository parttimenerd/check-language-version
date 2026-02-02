### Summary


Adds EdDSA signature support (e.g., Ed25519) via standard crypto APIs.

### Details

EdDSA (Edwards-curve Digital Signature Algorithm) is a modern signature scheme that includes variants like Ed25519 and Ed448.

Java supports EdDSA via standard JCA/JCE APIs in [`java.security`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/security/package-summary.html), typically through:

- [`KeyPairGenerator`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/security/KeyPairGenerator.html) with algorithms like `"Ed25519"`
- [`Signature`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/security/Signature.html) with algorithms like `"Ed25519"`

This allows generating key pairs and signing/verifying messages without third-party libraries.

### Example

```java
// Generate an Ed25519 key pair and sign a message
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;

class Example {
    byte[] demo() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("Ed25519"); // EdDSA key generation
        KeyPair kp = kpg.generateKeyPair();

        Signature s = Signature.getInstance("Ed25519"); // EdDSA signature
        s.initSign(kp.getPrivate());
        s.update("hello".getBytes(StandardCharsets.UTF_8)); // data to sign
        return s.sign();
    }
}
```

### Historical

Introduced in Java 15 via the platform crypto work that added EdDSA support.

### Links

- [KeyPairGenerator (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/security/KeyPairGenerator.html)
- [Signature (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/security/Signature.html)
- [JEP 339: Edwards-Curve Digital Signature Algorithm (EdDSA)](https://openjdk.org/jeps/339)