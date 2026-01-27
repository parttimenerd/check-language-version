// Java 8 edge case: Minimal lambda expression
// Test: Simple lambda that could be mistaken for method reference
// Expected Version: 8
// Required Features: LAMBDAS
public class Edge_SimpleLambda {
    Runnable r = () -> {};
}