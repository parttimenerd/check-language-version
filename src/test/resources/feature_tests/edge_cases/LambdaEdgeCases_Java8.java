// Edge case: Lambda variations
// Expected Version: 8
// Required Features: AUTOBOXING, COLLECTIONS_FRAMEWORK, GENERICS, LAMBDAS, METHOD_REFERENCES, STREAM_API
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class LambdaEdgeCases_Java8 {

    public void testBasicLambdas() {
        // No parameters
        Runnable r = () -> System.out.println("Hello");

        // Single parameter (no parens needed)
        Consumer<String> c1 = s -> System.out.println(s);

        // Single parameter with parens
        Consumer<String> c2 = (s) -> System.out.println(s);

        // Multiple parameters
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;

        // Block body
        BiFunction<Integer, Integer, Integer> complex = (a, b) -> {
            int result = a + b;
            return result * 2;
        };

        // Explicit types
        BiFunction<String, String, String> concat = (String a, String b) -> a + b;
    }

    public void testMethodReferences() {
        List<String> list = Arrays.asList("a", "b", "c");

        // Static method reference
        list.forEach(System.out::println);

        // Instance method reference on object
        String prefix = "prefix_";
        list.stream().map(prefix::concat);

        // Instance method reference on class
        list.stream().map(String::toUpperCase);

        // Constructor reference
        Supplier<ArrayList<String>> supplier = ArrayList::new;

        // Array constructor reference
        IntFunction<String[]> arrayCreator = String[]::new;
    }

    public void testLambdasInStreams() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        // Filter with lambda
        numbers.stream().filter(n -> n > 2);

        // Map with lambda
        numbers.stream().map(n -> n * 2);

        // Reduce with lambda
        numbers.stream().reduce(0, (a, b) -> a + b);

        // Chained operations
        int sum = numbers.stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * n)
            .reduce(0, Integer::sum);
    }

    public void testFunctionalInterfaces() {
        // Predicate
        Predicate<String> isEmpty = s -> s.isEmpty();
        Predicate<String> isLong = s -> s.length() > 10;
        Predicate<String> combined = isEmpty.or(isLong);

        // Function
        Function<String, Integer> length = String::length;
        Function<Integer, String> toString = Object::toString;
        Function<String, String> composed = length.andThen(toString);

        // Consumer
        Consumer<String> print = System.out::println;
        Consumer<String> printUpper = s -> System.out.println(s.toUpperCase());
        Consumer<String> both = print.andThen(printUpper);

        // Supplier
        Supplier<Double> random = Math::random;
    }
}