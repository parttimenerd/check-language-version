// Test: Switch pattern matching (Java 21)
// Expected Version: 21
// Required Features: SWITCH_EXPRESSIONS, SWITCH_PATTERN_MATCHING
class Tiny_SwitchPattern_Java21 {
    public String test(Object o) {
        return switch (o) {
            case Integer i -> "int: " + i;
            case String s -> "str: " + s;
            default -> "other";
        };
    }
}