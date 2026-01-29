// Test: Var in lambda (Java 11)
// Expected Version: 11
// Required Features: GENERICS, LAMBDAS, VAR_IN_LAMBDA

import java.util.function.Predicate;

class Tiny_VarLambda_Java11 {
    Predicate<String> f = (var a) -> a.isEmpty();
}