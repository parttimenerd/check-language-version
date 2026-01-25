// Java 21 combination: Records + Sealed classes + Pattern matching (Advanced)
// Test: Advanced combination of records with sealed classes and pattern matching
// Expected Version: 21
// Required Features: RECORDS, SEALED_CLASSES, PATTERN_MATCHING_INSTANCEOF, RECORD_PATTERNS, SWITCH_PATTERN_MATCHING
public class Combo_RecordSealedPattern_Java21_Advanced {
    sealed interface Shape permits Circle, Square {}
    record Circle(double radius) implements Shape {}
    record Square(double side) implements Shape {}

    void test(Shape s) {
        if (s instanceof Circle(var r)) {
            System.out.println(r);
        }
        switch (s) {
            case Circle(var r) -> System.out.println(r);
            case Square(var side) -> System.out.println(side);
        }
    }
}