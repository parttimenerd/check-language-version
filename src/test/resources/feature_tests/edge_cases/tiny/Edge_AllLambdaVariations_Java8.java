// Java 8 edge case: All lambda variations
// Test: Testing all variations of lambda expressions
// Expected Version: 8
// Required Features: GENERICS, LAMBDAS
import java.util.function.*;
public class Edge_AllLambdaVariations_Java8 {
    void test() {
        Runnable r1 = () -> {};
        Runnable r2 = () -> System.out.println("hi");
        Consumer<String> c1 = (String s) -> System.out.println(s);
        Consumer<String> c2 = s -> System.out.println(s);
        BiFunction<Integer, Integer, Integer> f = (a, b) -> a + b;
    }
}