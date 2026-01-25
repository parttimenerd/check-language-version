// Java 8 combination: Stream + Optional + Method reference + Lambda
// Test: Combination of streams with Optional, method references, and lambdas
// Expected Version: 8
// Required Features: STREAM_API, OPTIONAL, METHOD_REFERENCES, LAMBDAS
import java.util.*;
import java.util.stream.Stream;
public class Combo_StreamOptionalRefLambda_Java8 {
    public Optional<String> process(List<String> list) {
        Stream<String> stream = list.stream();
        return stream
            .filter(s -> s.length() > 3)
            .map(String::toUpperCase)
            .reduce((a, b) -> a + "," + b);
    }
}