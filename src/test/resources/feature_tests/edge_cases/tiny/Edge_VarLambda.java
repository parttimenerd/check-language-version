// Java 11 edge case: var in lambda parameter
// Test: var keyword in lambda (not Java 10!)
// Expected Version: 11
// Required Features: VAR_IN_LAMBDA, LAMBDAS, GENERICS
public class Edge_VarLambda {
    java.util.function.Function<String,String> f = (var s) -> s;
}