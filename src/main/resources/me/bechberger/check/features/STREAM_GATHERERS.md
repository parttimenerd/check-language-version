### Summary


Adds Stream Gatherers to extend stream pipelines with custom intermediate operations.

### Details

Stream Gatherers enhance the [`Stream`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html) API with a new intermediate operation that can express custom transformations which are not well covered by the fixed set of built-in intermediate operations.

The new operation is `Stream::gather(Gatherer)`, and it is to intermediate operations what [`Stream.collect(Collector)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#collect(java.util.stream.Collector)) is to terminal operations.

A *gatherer* (an instance of `java.util.stream.Gatherer`) can transform elements in flexible ways:

- one-to-one (like `map`)
- one-to-many (like `flatMap` or `filter` that emits 0 or 1)
- many-to-one (like running aggregation)
- many-to-many (like fixed or sliding windows)

Gatherers can be stateful, can short-circuit (useful for turning infinite streams into finite ones), and can optionally support parallel evaluation.

The JDK provides built-in gatherers in the `java.util.stream.Gatherers` utility class, including:

- `fold` (incremental aggregation)
- `mapConcurrent` (concurrent mapping up to a limit)
- `scan` (running accumulation)
- `windowFixed` (fixed-size windows)
- `windowSliding` (sliding windows)

### Example

```java
// Find suspicious pairs of consecutive readings using a sliding window gatherer
import java.time.Instant;
import java.util.List;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

record Reading(Instant obtainedAt, int kelvins) {
    Reading(String time, int kelvins) {
        this(Instant.parse(time), kelvins);
    }

    static Stream<Reading> loadRecentReadings() {
        return Stream.of(
                new Reading("2023-09-21T10:15:30.00Z", 310),
                new Reading("2023-09-21T10:15:31.00Z", 312),
                new Reading("2023-09-21T10:15:32.00Z", 350),
                new Reading("2023-09-21T10:15:33.00Z", 310)
        );
    }
}

class Example {
    static boolean isSuspicious(Reading previous, Reading next) {
        return next.obtainedAt().isBefore(previous.obtainedAt().plusSeconds(5))
                && (next.kelvins() > previous.kelvins() + 30
                    || next.kelvins() < previous.kelvins() - 30);
    }

    static List<List<Reading>> findSuspicious(Stream<Reading> source) {
        return source.gather(Gatherers.windowSliding(2))
                .filter(window -> window.size() == 2
                        && isSuspicious(window.get(0), window.get(1)))
                .toList(); // emit suspicious pairs
    }
}
```

### Historical

Stream gatherers were introduced as a preview feature in Java 22 and re-previewed in Java 23, then finalized in Java 24 (JEP 485).

### Links

- [JEP 485: Stream Gatherers](https://openjdk.org/jeps/485)
- [Stream.gather(Gatherer) (Oracle Javadoc, Java 24)](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/util/stream/Stream.html#gather(java.util.stream.Gatherer))
- [Gatherers (Oracle Javadoc, Java 24)](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/util/stream/Gatherers.html)
- [Stream (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html)