// Java 11 combination: Var + Lambda + Method reference + Collection factory + Stream
// Test: Combination of var in lambdas with method references, collection factory methods, and streams
// Expected Version: 11
// Required Features: VAR, VAR_IN_LAMBDA, LAMBDAS, METHOD_REFERENCES, COLLECTION_FACTORY_METHODS
// Note: STREAM_API is not required as it's only detected via explicit Stream type usage, not .stream() calls
import java.util.*;
import java.util.stream.*;

public class Combo_VarLambdaRefFactoryStream_Java11 {
    void test() {
        var list = List.of("a", "b", "c");
        list.stream()
            .map((var s) -> s.toUpperCase())
            .map(String::toLowerCase)
            .forEach(System.out::println);
    }
}