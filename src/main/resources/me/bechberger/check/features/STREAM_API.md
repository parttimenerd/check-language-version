### Summary

Adds the Streams API in [`java.util.stream`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html) for functional-style bulk operations on sequences.

### Details

A stream represents a sequence of elements supporting **pipeline** processing (map/filter/reduce) and optional parallel execution.

Streams are typically created from collections (e.g., via [`Collection.stream()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collection.html#stream())) or from specialized factories like [`Stream.of(T...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#of(T...)).

Key concepts:

- **Intermediate operations** like [`Stream.map(Function)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#map(java.util.function.Function)) and [`Stream.filter(Predicate)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#filter(java.util.function.Predicate)) are lazy.
- **Terminal operations** like [`Stream.collect(Collector)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#collect(java.util.stream.Collector)) trigger evaluation.
- Streams are not reusable; a terminal operation consumes the stream.

### Example

```java
// Compute the sum of squares of even numbers
import java.util.List;

class Example {
    int demo(List<Integer> xs) {
        return xs.stream()
                .filter(x -> x % 2 == 0) // keep evens
                .mapToInt(x -> x * x)
                .sum();
    }
}
```

### Historical

Introduced in Java 8 as part of the platform changes around lambdas (commonly grouped under JSR 335).

### Links

- [Package java.util.stream (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html)
- [Stream (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html)
- [Collection.stream() (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collection.html#stream())