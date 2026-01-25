// Java 8 combination: Inner class + Generics + Lambda
// Test: Combination of inner classes with generics and lambda expressions
// Expected Version: 8
// Required Features: INNER_CLASSES, GENERICS, LAMBDAS
import java.util.function.*;
public class Combo_InnerGenericsLambda_Java8 {
    class Inner<T> {
        Function<T, String> f = t -> t.toString();
    }
}