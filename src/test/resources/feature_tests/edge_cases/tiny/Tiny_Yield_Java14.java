// Test: Yield in switch (Java 14)
// Expected Version: 14
// Required Features: YIELD
public class Tiny_Yield_Java14 {
    public int test(int x) {
        return switch (x) {
            case 1 -> 10;
            default -> { yield x * 2; }
        };
    }
}