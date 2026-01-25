// Java 21 combination: Records + Sealed classes + Pattern matching + Switch + Text blocks
// Test: Combination of records, sealed classes, pattern matching, switch expressions, and text blocks
// Expected Version: 21
// Required Features: RECORDS, SEALED_CLASSES, RECORD_PATTERNS, SWITCH_PATTERN_MATCHING, TEXT_BLOCKS
public class Combo_RecordsSealedPatternSwitchText_Java21 {
    sealed interface Shape permits Circle, Square {}
    record Circle(double radius) implements Shape {}
    record Square(double side) implements Shape {}

    String describe(Shape s) {
        return switch (s) {
            case Circle(double r) -> """
                Circle with radius: %f
                Area: %f
                """.formatted(r, Math.PI * r * r);
            case Square(double side) -> """
                Square with side: %f
                Area: %f
                """.formatted(side, side * side);
        };
    }
}