// Java 21 feature: Record patterns
// Expected Version: 21
// Required Features: PATTERN_MATCHING_INSTANCEOF, RECORDS, RECORD_PATTERNS, SWITCH_EXPRESSIONS, SWITCH_PATTERN_MATCHING
public class Java21_RecordPatterns {
    record Point(int x, int y) {}
    record Rectangle(Point topLeft, Point bottomRight) {}

    public void process(Object obj) {
        // Record pattern in instanceof
        if (obj instanceof Point(int x, int y)) {
            System.out.println("Point at (" + x + ", " + y + ")");
        }

        // Nested record patterns
        if (obj instanceof Rectangle(Point(int x1, int y1), Point(int x2, int y2))) {
            System.out.println("Rectangle from (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ")");
        }
    }

    public int calculateArea(Object shape) {
        return switch (shape) {
            case Rectangle(Point(int x1, int y1), Point(int x2, int y2)) ->
                Math.abs(x2 - x1) * Math.abs(y2 - y1);
            default -> 0;
        };
    }
}