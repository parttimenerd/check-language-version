// Edge case: Pattern matching variations
// Expected Version: 21
// Required Features: PATTERN_MATCHING_INSTANCEOF, RECORD_PATTERNS, SWITCH_PATTERN_MATCHING
public class PatternMatchingEdgeCases_Java21 {

    record Point(int x, int y) {}
    record Line(Point start, Point end) {}

    public void testInstanceofPatterns(Object obj) {
        if (obj instanceof String s) {
            System.out.println("String: " + s.length());
        }

        if (obj instanceof Integer i && i > 0) {
            System.out.println("Positive: " + i);
        }
    }

    public void testRecordPatterns(Object obj) {
        if (obj instanceof Point(int x, int y)) {
            System.out.println("Point at " + x + ", " + y);
        }

        if (obj instanceof Line(Point(int x1, int y1), Point(int x2, int y2))) {
            System.out.println("Line");
        }
    }

    public String switchPatterns(Object obj) {
        return switch (obj) {
            case null -> "null";
            case String s -> "string: " + s;
            case Integer i when i < 0 -> "negative";
            case Integer i -> "positive: " + i;
            case Point(int x, int y) -> "point";
            default -> "unknown";
        };
    }
}