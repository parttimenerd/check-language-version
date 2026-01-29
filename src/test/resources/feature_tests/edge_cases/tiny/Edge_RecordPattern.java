// Java 21 edge case: Record pattern in instanceof
// Test: Deconstructing record in instanceof
// Expected Version: 21
// Required Features: RECORD_PATTERNS, PATTERN_MATCHING_INSTANCEOF, RECORDS
class Edge_RecordPattern {
    record Point(int x, int y) {}
    void test(Object o) {
        if (o instanceof Point(int x, int y)) {
            System.out.println(x + y);
        }
    }
}