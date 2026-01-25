// Test: Var in lambda (Java 11)
// Expected Version: 11
// Required Features: VAR_IN_LAMBDA

import java.util.function.Predicate;

public class Tiny_VarLambda_Java11 {
    Predicate<String> f = (var a) -> a.isEmpty();
}