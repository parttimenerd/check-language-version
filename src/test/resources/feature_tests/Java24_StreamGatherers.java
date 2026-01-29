// Java 24 feature: Stream Gatherers (JEP 485)
// Expected Version: 24
// Required Features: AUTOBOXING, COLLECTIONS_FRAMEWORK, COLLECTION_FACTORY_METHODS, GENERICS, LAMBDAS, METHOD_REFERENCES, STREAM_API, STREAM_GATHERERS, VAR
// Gatherers allow creating custom intermediate operations for streams
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;
import java.util.List;
import java.util.stream.Stream;

class Java24_StreamGatherers {
    public void testGatherers() {
        // Using built-in gatherers
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // windowFixed - groups elements into fixed-size windows
        var windows = numbers.stream()
            .gather(Gatherers.windowFixed(3))
            .toList();
        System.out.println("Windows: " + windows);  // [[1,2,3], [4,5,6], [7,8,9]]

        // windowSliding - sliding window
        var sliding = numbers.stream()
            .gather(Gatherers.windowSliding(3))
            .toList();
        System.out.println("Sliding: " + sliding);

        // fold - accumulate with initial value
        var sum = numbers.stream()
            .gather(Gatherers.fold(() -> 0, Integer::sum))
            .findFirst()
            .orElse(0);
        System.out.println("Sum: " + sum);

        // scan - running accumulation
        var running = numbers.stream()
            .gather(Gatherers.scan(() -> 0, Integer::sum))
            .toList();
        System.out.println("Running sum: " + running);
    }
}