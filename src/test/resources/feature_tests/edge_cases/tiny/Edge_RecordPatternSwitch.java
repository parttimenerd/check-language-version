// Java 21 edge case: Record pattern in switch
// Test: Record deconstruction in switch case
// Expected Version: 21
// Required Features: RECORDS, RECORD_PATTERNS, SWITCH_EXPRESSIONS, SWITCH_PATTERN_MATCHING
record Edge_RecordPatternSwitch(int x, int y) {}
public class Tiny_RecPattern_Java21 {
    void test(Object o) {
        switch (o) {
            case Edge_RecordPatternSwitch(int x, int y) -> System.out.println(x + y);
            case String s -> System.out.println(s);
            default -> System.out.println("other");
        }
    }
}