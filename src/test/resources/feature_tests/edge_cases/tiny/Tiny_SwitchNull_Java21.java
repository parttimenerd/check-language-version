// Test: Switch null/default (Java 21)
// Expected Version: 21
// Required Features: SWITCH_NULL_DEFAULT
public class Tiny_SwitchNull_Java21 {
    public String test(Object o) {
        return switch (o) {
            case null -> "null";
            case String s -> s;
            case null, default -> "other";
        };
    }
}