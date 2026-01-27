// Java 16 edge case: Pattern matching instanceof in expression
// Test: instanceof with pattern in boolean expression
// Expected Version: 16
// Required Features: PATTERN_MATCHING_INSTANCEOF
public class Edge_InstanceofPattern {
    boolean b = "x" instanceof String s && s.isEmpty();
}