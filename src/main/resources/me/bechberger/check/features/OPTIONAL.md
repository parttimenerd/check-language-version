### Summary


Adds [`Optional`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html) as a container type for optional values.

### Details

[`Optional<T>`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html) is a type that either contains a non-null value (`present`) or contains no value (`empty`).

It is commonly used as:

- a return type to represent “might be absent” without returning `null`
- a fluent way to transform values via methods like [`Optional.map(Function)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#map(java.util.function.Function))

Common operations include:

- creating optionals with [`Optional.of(T)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#of(T)), [`Optional.ofNullable(T)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#ofNullable(T)), and [`Optional.empty()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#empty())
- extracting defaults with [`Optional.orElse(T)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#orElse(T)) or throwing with [`Optional.orElseThrow()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#orElseThrow())

### Example

```java
// Use Optional to avoid returning null
import java.util.Optional;

class Example {
    Optional<String> find(boolean ok) {
        return ok ? Optional.of("x") : Optional.empty(); // explicit absence/presence
    }

    String demo() {
        return find(false).orElse("default"); // fallback value
    }
}
```

### Historical

Introduced in Java 8 (JSR 335 era) alongside streams and functional interfaces. It is widely used today as a return type, but is generally discouraged for fields and serialization.

### Links

- [Optional (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html)
- [Optional.map(Function) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#map(java.util.function.Function))
- [Package java.util (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/package-summary.html)