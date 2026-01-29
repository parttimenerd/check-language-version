// Java 8 feature: Method references
// Expected Version: 8
// Required Features: COLLECTIONS_FRAMEWORK, GENERICS, METHOD_REFERENCES
import java.util.function.*;
import java.util.*;
class Java8_MethodReferences {
    public void method() {
        List<String> list = Arrays.asList("a", "b", "c");
        // Method reference to static method
        list.forEach(System.out::println);
        // Method reference to instance method
        list.stream().map(String::toUpperCase).forEach(System.out::println);
        // Method reference to constructor
        Supplier<ArrayList<String>> supplier = ArrayList::new;
        // Method reference to instance method of arbitrary object
        Comparator<String> comparator = String::compareToIgnoreCase;
    }
}