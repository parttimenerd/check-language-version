// Java 21 edge case: Complex switch with all features
// Test: Testing switch with expressions, patterns, null/default, and yield
// Expected Version: 21
// Required Features: SWITCH_EXPRESSIONS, SWITCH_PATTERN_MATCHING, SWITCH_NULL_DEFAULT, YIELD
public class Edge_ComplexSwitchAllFeatures {
    Object test(Object obj) {
        return switch (obj) {
            case String s when s.length() > 5 -> s.toUpperCase();
            case String s -> s;
            case Integer i -> {
                yield i * 2;
            }
            case null, default -> "unknown";
        };
    }
}