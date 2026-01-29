// Java 14 edge case: Yield statement in minimal context
// Test: yield keyword that could be confused with return
// Expected Version: 14
// Required Features: YIELD, SWITCH_EXPRESSIONS
class Edge_YieldOnly {
    int test(int x) {
        return switch (x) {
            case 1 -> 10;
            default -> { yield 0; }
        };
    }
}