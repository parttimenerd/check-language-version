// Java 21 combination: Records + Pattern matching + Switch
// Test: Combination of records with pattern matching in switch statements
// Expected Version: 21
// Required Features: RECORDS, RECORD_PATTERNS, SWITCH_EXPRESSIONS, SWITCH_NULL_DEFAULT, SWITCH_PATTERN_MATCHING
class Combo_RecordsPatternSwitch {
    record Point(int x, int y) {}

    public String test(Object o) {
        return switch (o) {
            case Point(int x, int y) when x > 0 -> "positive x";
            case Point(int x, int y) -> "point at " + x + "," + y;
            case String s -> s;
            case null, default -> "other";
        };
    }
}