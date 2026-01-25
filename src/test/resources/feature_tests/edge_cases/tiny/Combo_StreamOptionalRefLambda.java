// Java 8 combination: Stream + Optional + Method reference + Lambda
// Test: Combination of streams with Optional, method references, and lambdas
// Expected Version: 8
// Required Features: COLLECTIONS_FRAMEWORK, GENERICS, LAMBDAS, METHOD_REFERENCES, OPTIONAL, STREAM_API
import java.util.*;
import java.util.stream.Stream;
public class Combo_StreamOptionalRefLambda {
    public Optional<String> process(List<String> list) {
        Stream<String> stream = list.stream();
        return stream
            .filter(s -> s.length() > 3)
            .map(String::toUpperCase)
            .reduce((a, b) -> a + "," + b);
    }
}