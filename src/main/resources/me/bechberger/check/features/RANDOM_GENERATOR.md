### Summary


Adds the random number generator interfaces in [`java.util.random`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/random/package-summary.html).

### Details

Java 17 introduced a set of interfaces and implementations to standardize and expand random number generation.

The key interface is [`RandomGenerator`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/random/RandomGenerator.html), with many algorithm implementations available under `java.util.random`.

This provides a more flexible model than the legacy [`java.util.Random`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Random.html), including better support for choosing specific algorithms.

### Example

```java
// Generate random numbers using a specific algorithm
import java.util.random.RandomGenerator;

class Example {
    int demo() {
        RandomGenerator r = RandomGenerator.getDefault(); // default algorithm
        return r.nextInt(10); // range [0, 10)
    }
}
```

### Historical

Introduced in Java 17 via JEP 356 to provide a standard set of random generator interfaces and implementations.

### Links

- [Package java.util.random (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/random/package-summary.html)
- [RandomGenerator (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/random/RandomGenerator.html)
- [JEP 356: Enhanced Pseudo-Random Number Generators](https://openjdk.org/jeps/356)