// Tiny: Switch expression (Java 14)
// Expected Version: 14
// Required Features: SWITCH_EXPRESSIONS

class Tiny_SwitchExprSimple_Java14 {
    int test(int x) {
        return switch (x) {
            case 1 -> 10;
            default -> 0;
        };
    }
}