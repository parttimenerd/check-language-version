// Java 16 combination: Pattern matching instanceof + Records
// Test: Combination of pattern matching for instanceof with records
// Expected Version: 16
// Required Features: PATTERN_MATCHING_INSTANCEOF, RECORDS
public class Combo_PatternInstanceofRecords_Java16 {
    record Point(int x, int y) {}

    void test(Object obj) {
        if (obj instanceof Point p) {
            System.out.println(p.x() + p.y());
        }
    }
}