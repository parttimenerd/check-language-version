// Java 14 edge case: Yield in nested switch
// Test: Testing yield statements in nested switch expressions
// Expected Version: 14
// Required Features: YIELD, SWITCH_EXPRESSIONS
public class Edge_YieldNested_Java14 {
    int test(int x, int y) {
        return switch (x) {
            case 1 -> switch (y) {
                case 1 -> { yield 11; }
                default -> { yield 10; }
            };
            default -> { yield 0; }
        };
    }
}