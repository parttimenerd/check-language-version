### Summary


Adds quantum-resistant (post-quantum) Key Encapsulation Mechanism (KEM) support in the standard crypto APIs.

### Details

Post-quantum (quantum-resistant) KEMs are designed to remain secure against attackers with large-scale quantum computers.

In Java, KEM functionality is exposed via [`javax.crypto.KEM`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/crypto/KEM.html). Providers may supply post-quantum KEM algorithms (for example, ML-KEM / Kyber variants).

Availability of specific algorithm names depends on the bundled providers and the Java release.

### Example

```java
// Use a KEM instance that may be backed by a post-quantum algorithm (illustrative)
import javax.crypto.KEM;

class Example {
    KEM demo() throws Exception {
        return KEM.getInstance("ML-KEM"); // algorithm name may vary by release/provider
    }
}
```

### Historical

Introduced in Java 24 as part of the platform's evolving support for post-quantum cryptography.

### Links

- [KEM (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/crypto/KEM.html)
- [JEP index (OpenJDK)](https://openjdk.org/jeps/)