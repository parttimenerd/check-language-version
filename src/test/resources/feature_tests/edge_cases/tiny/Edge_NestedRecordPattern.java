// Java 21 edge case: Nested record patterns
// Test: Testing deeply nested record patterns in instanceof
// Expected Version: 21
// Required Features: RECORD_PATTERNS, PATTERN_MATCHING_INSTANCEOF, RECORDS
class Edge_NestedRecordPattern {
    record Point(int x, int y) {}
    record Line(Point start, Point end) {}

    public void test(Object o) {
        if (o instanceof Line(Point(int x1, int y1), Point(int x2, int y2))) {
            System.out.println(x1 + y1 + x2 + y2);
        }
    }
}