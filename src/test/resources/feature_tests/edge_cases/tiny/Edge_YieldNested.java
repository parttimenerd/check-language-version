// Edge: Yield in nested switch (Java 14)
// Expected Version: 14
// Required Features: YIELD, SWITCH_EXPRESSIONS
public class Edge_YieldNested {
    public int test(int x, int y) {
        return switch (x) {
            case 1 -> switch (y) {
                case 1 -> 11;
                case 2 -> { yield 12; }
                default -> 10;
            };
            default -> { yield x + y; }
        };
    }
}