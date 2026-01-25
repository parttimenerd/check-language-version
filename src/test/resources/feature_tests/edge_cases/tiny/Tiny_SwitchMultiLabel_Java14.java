// Test: Switch multiple labels (Java 14)
// Expected Version: 14
// Required Features: SWITCH_EXPRESSIONS, SWITCH_MULTIPLE_LABELS
public class Tiny_SwitchMultiLabel_Java14 {
    public int test(String s) {
        return switch (s) {
            case "a", "b", "c" -> 1;
            default -> 0;
        };
    }
}