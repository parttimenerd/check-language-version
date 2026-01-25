// Test: Switch expressions (Java 14)
// Expected Version: 14
// Required Features: SWITCH_EXPRESSIONS
public class Tiny_SwitchExpr_Java14 {
    public int test(String s) {
        return switch (s) {
            case "a" -> 1;
            case "b" -> 2;
            default -> 0;
        };
    }
}