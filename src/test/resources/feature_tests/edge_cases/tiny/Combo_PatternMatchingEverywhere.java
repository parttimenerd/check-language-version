// Java 21 combination: Pattern matching everywhere
// Test: Pattern matching in instanceof, switch, and record patterns
// Expected Version: 21
// Required Features: PATTERN_MATCHING_INSTANCEOF, SWITCH_PATTERN_MATCHING, RECORD_PATTERNS, RECORDS
public class Combo_PatternMatchingEverywhere {
    record Point(int x, int y) {}

    void test(Object obj) {
        if (obj instanceof Point p) {
            System.out.println(p);
        }

        switch (obj) {
            case Point(int x, int y) -> System.out.println(x + y);
            case String s -> System.out.println(s);
            default -> System.out.println("other");
        }
    }
}