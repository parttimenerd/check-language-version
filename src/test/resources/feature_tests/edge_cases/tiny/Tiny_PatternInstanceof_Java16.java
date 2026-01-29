// Test: Pattern matching instanceof (Java 16)
// Expected Version: 16
// Required Features: PATTERN_MATCHING_INSTANCEOF
class Tiny_PatternInstanceof_Java16 {
    public void test(Object o) {
        if (o instanceof String s) {
            System.out.println(s.length());
        }
    }
}