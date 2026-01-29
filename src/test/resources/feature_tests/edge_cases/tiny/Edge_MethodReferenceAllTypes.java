// Java 8 edge case: All types of method references
// Test: Testing static, instance, constructor, and arbitrary object method references
// Expected Version: 8
// Required Features: GENERICS, METHOD_REFERENCES
import java.util.function.*;

class Edge_MethodReferenceAllTypes {
    void test() {
        // Static method reference
        Function<String, Integer> f1 = Integer::parseInt;
        // Instance method reference on specific object
        Supplier<String> f2 = "hello"::toUpperCase;
        // Constructor reference
        Supplier<String> f3 = String::new;
        // Arbitrary object instance method reference
        BiPredicate<String, String> f4 = String::equals;
    }
}