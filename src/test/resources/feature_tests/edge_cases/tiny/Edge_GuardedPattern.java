// Java 21 edge case: Guarded patterns in switch
// Test: Testing guarded patterns (when clauses) in switch expressions
// Expected Version: 21
// Required Features: SWITCH_EXPRESSIONS, SWITCH_PATTERN_MATCHING
public class Edge_GuardedPattern {
    public String test(Object o) {
        return switch (o) {
            case Integer i when i > 0 -> "positive";
            case Integer i when i < 0 -> "negative";
            case Integer i -> "zero";
            case String s when s.isEmpty() -> "empty";
            case String s -> s;
            default -> "other";
        };
    }
}