// Java 8 edge case: All types of method references
// Test: Testing all types of method references (static, instance, constructor, arbitrary object)
// Expected Version: 8
// Required Features: METHOD_REFERENCES
import java.util.function.*;
public class Edge_AllMethodReferences_Java8 {
    void test() {
        // Static method reference
        Function<String, Integer> f1 = Integer::parseInt;
        // Instance method reference
        Supplier<String> f2 = "hello"::toUpperCase;
        // Constructor reference
        Supplier<String> f3 = String::new;
        // Arbitrary object method reference
        BiPredicate<String, String> f4 = String::equals;
    }
}