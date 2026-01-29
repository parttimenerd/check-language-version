// Test: Record patterns (Java 21)
// Expected Version: 21
// Required Features: PATTERN_MATCHING_INSTANCEOF, RECORDS, RECORD_PATTERNS
class Tiny_RecordPattern_Java21 {
    record Point(int x, int y) {}
    public void test(Object o) {
        if (o instanceof Point(int x, int y)) {
            System.out.println(x + y);
        }
    }
}