// Java 22 edge case: Unnamed variables in lambda
// Test: Underscores as lambda parameters
// Expected Version: 22
// Required Features: UNNAMED_VARIABLES
public class Edge_UnnamedLambda {
    java.util.function.BiConsumer<String,String> c = (_, _) -> {};
}