// Java 8 edge case: All lambda variations
// Test: Testing all variations of lambda syntax
// Expected Version: 8
// Required Features: GENERICS, LAMBDAS
import java.util.function.*;

class Edge_LambdaAllVariations {
    void test() {
        Runnable r1 = () -> {};
        Runnable r2 = () -> System.out.println("hi");
        Consumer<String> c1 = (String s) -> System.out.println(s);
        Consumer<String> c2 = s -> System.out.println(s);
        BiFunction<Integer, Integer, Integer> f = (a, b) -> a + b;
        Supplier<String> s = () -> "test";
    }
}