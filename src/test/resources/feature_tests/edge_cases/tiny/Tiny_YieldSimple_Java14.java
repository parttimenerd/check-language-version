// Tiny: Yield (Java 14)
// Expected Version: 14
// Required Features: YIELD

public class Tiny_YieldSimple_Java14 {
    int test(int x) {
        return switch (x) {
            case 1 -> 10;
            default -> { yield x * 2; }
        };
    }
}