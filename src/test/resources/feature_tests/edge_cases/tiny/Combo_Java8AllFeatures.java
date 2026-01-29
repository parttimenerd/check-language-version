// Java 8 combination: All major features
// Test: Combination of lambdas, method references, default/static interface methods, streams, Optional, and date/time API
// Expected Version: 8
// Required Features: COLLECTIONS_FRAMEWORK, DATE_TIME_API, DEFAULT_INTERFACE_METHODS, GENERICS, INNER_CLASSES, LAMBDAS, METHOD_REFERENCES, OPTIONAL, STATIC_INTERFACE_METHODS, STREAM_API
import java.util.*;
import java.util.stream.Stream;
import java.time.*;

class Combo_Java8AllFeatures {
    interface Processor {
        default void process() {
            run(() -> System.out.println("processing"));
        }
        static void log(String msg) { System.out.println(msg); }
        void run(Runnable r);
    }

    void test() {
        Optional<String> opt = Optional.of("test");
        List<String> items = Arrays.asList("a", "b", "c");
        // Explicit Stream type usage for detection
        Stream<String> stream = items.stream();
        stream.map(String::toUpperCase)
            .forEach(System.out::println);
        LocalDate date = LocalDate.now();
    }
}