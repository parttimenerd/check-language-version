// Tiny: Yield (Java 14)
// Expected Version: 14
// Required Features: SWITCH_EXPRESSIONS, YIELD

class Tiny_YieldSimple_Java14 {
    int test(int x) {
        return switch (x) {
            case 1 -> 10;
            default -> { yield x * 2; }
        };
    }
}