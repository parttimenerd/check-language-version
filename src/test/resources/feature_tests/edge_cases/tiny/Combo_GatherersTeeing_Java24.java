// Java 24 combination: Stream gatherers + Collectors teeing
// Test: Combination of stream gatherers with collectors teeing
// Expected Version: 24
// Required Features: AUTOBOXING, COLLECTORS_TEEING, LAMBDAS, STREAM_API, STREAM_GATHERERS
import java.util.stream.*;
import java.util.stream.Gatherers;
class Combo_GatherersTeeing_Java24 {
    void test() {
        Stream.of(1, 2, 3)
            .collect(Collectors.teeing(
                Collectors.counting(),
                Collectors.summingInt(i -> i),
                (count, sum) -> sum / count
            ));
        Stream.of(1, 2, 3).gather(Gatherers.fold(() -> 0, (a, b) -> a + b));
    }
}