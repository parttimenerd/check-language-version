// Java 14 edge case: Minimal switch expression
// Test: Switch expression with arrow but no yield
// Expected Version: 14
// Required Features: SWITCH_EXPRESSIONS
public class Edge_SwitchExpressionMinimal {
    int test(int x) { return switch(x) { default -> 0; }; }
}