// Test: Lambda expressions (Java 8)
// Expected Version: 8
// Required Features: COLLECTIONS_FRAMEWORK, GENERICS, LAMBDAS
import java.util.function.*;
import java.util.*;

class Java8_Lambdas {
    public void testLambdas() {
        Runnable r = () -> System.out.println("Hello");

        Consumer<String> consumer = s -> System.out.println(s);

        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;

        Comparator<String> comparator = (s1, s2) -> s1.length() - s2.length();

        List<String> list = Arrays.asList("a", "bb", "ccc");
        list.forEach(s -> System.out.println(s));
    }
}