// Tiny: Record pattern (Java 21)
// Expected Version: 21
// Required Features: PATTERN_MATCHING_INSTANCEOF, RECORDS, RECORD_PATTERNS

record Point(int x, int y) {}

class Tiny_RecPattern_Java21 {
    void test(Object o) {
        if (o instanceof Point(int x, int y)) {
            System.out.println(x + y);
        }
    }
}