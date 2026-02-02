### Summary


Adds [`Collectors.teeing(...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html#teeing(java.util.stream.Collector,java.util.stream.Collector,java.util.function.BiFunction)) to combine the results of two collectors in one pass.

### Details

[`Collectors.teeing(...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html#teeing(java.util.stream.Collector,java.util.stream.Collector,java.util.function.BiFunction)) is a collector that runs two downstream collectors over the same stream elements and then merges their results using a finishing function.

This is useful when you want to compute two aggregated values at once, for example:

- count + sum
- min + max
- summary statistics + derived value

It avoids traversing the stream twice or materializing intermediate collections.

### Example

```java
// Compute average = sum / count in a single collect() call
import java.util.List;
import java.util.stream.Collectors;

class Example {
    double demo(List<Integer> xs) {
        return xs.stream().collect(Collectors.teeing(
                Collectors.summingInt(x -> x), // sum all elements
                Collectors.counting(),         // count elements
                (sum, count) -> count == 0 ? 0.0 : ((double) sum) / count
        ));
    }
}
```

### Historical

Introduced in Java 12 as part of small improvements to the Streams API.

### Links

- [Collectors.teeing(...) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html#teeing(java.util.stream.Collector,java.util.stream.Collector,java.util.function.BiFunction))
- [Collectors (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html)
- [Stream.collect(Collector) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#collect(java.util.stream.Collector))