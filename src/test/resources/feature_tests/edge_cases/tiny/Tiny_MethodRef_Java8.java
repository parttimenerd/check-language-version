// Test: Method references (Java 8)
// Expected Version: 8
// Required Features: GENERICS, METHOD_REFERENCES
import java.util.function.*;
class Tiny_MethodRef_Java8 {
    Consumer<String> c = System.out::println;
}