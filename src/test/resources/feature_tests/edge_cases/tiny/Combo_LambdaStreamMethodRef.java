// Java 8 combination: Lambda + Method reference + Stream
// Test: Combination of lambda expressions with method references and streams
// Expected Version: 8
// Required Features: AUTOBOXING, LAMBDAS, METHOD_REFERENCES, STREAM_API
import java.util.stream.*;
public class Combo_LambdaStreamMethodRef {
    public void test() {
        Stream.of(1, 2, 3)
            .filter(x -> x > 1)
            .map(String::valueOf)
            .forEach(System.out::println);
    }
}