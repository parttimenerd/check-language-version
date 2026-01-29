// Java 8 combination: Lambda + Method reference + Stream
// Test: Combination of lambdas with method references and streams
// Expected Version: 8
// Required Features: COLLECTIONS_FRAMEWORK, GENERICS, LAMBDAS, METHOD_REFERENCES, STREAM_API
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
class Combo_LambdaMethodRefStream_Java8 {
    void test() {
        List<String> list = Arrays.asList("a", "b", "c");
        // Explicit Stream type for STREAM_API detection
        Stream<String> stream = list.stream();
        // Lambda expression for LAMBDAS detection
        stream.filter(s -> s.length() > 0)
            .map(String::toUpperCase)  // method reference
            .forEach(System.out::println);  // method reference
    }
}