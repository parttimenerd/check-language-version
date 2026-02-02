### Summary


Adds a Key Derivation Function (KDF) API for deriving keys from input material.

### Details

Key derivation functions are used to derive one or more cryptographic keys from input material such as a shared secret or password.

This feature adds a standardized API surface for KDF usage in Java's crypto stack, intended to reduce ad-hoc implementations and align with modern protocols.

### Example

```java
// KDF usage (illustrative; exact API depends on the Java release)
class Example {
    void demo() {
        // Use the standard KDF API to derive a key from input material.
        // (See JEP 510 for the finalized API and examples.)
    }
}
```

### Historical

Introduced in Java 25 via JEP 510.

### Links

- [JEP 510: Key Derivation Function API](https://openjdk.org/jeps/510)
- [Package javax.crypto (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/crypto/package-summary.html)