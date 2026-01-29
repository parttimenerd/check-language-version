// Java 21 edge case: Pattern instanceof with record and guard
// Test: Testing pattern matching for instanceof with record patterns and guards
// Expected Version: 21
// Required Features: PATTERN_MATCHING_INSTANCEOF, RECORDS, RECORD_PATTERNS
class Edge_PatternInstanceofRecordGuard {
    record Point(int x, int y) {}

    void test(Object obj) {
        if (obj instanceof Point(int x, int y) && x > 0) {
            System.out.println(x + y);
        }
    }
}