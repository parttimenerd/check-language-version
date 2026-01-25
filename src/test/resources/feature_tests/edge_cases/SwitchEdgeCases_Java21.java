// Edge case: Switch expression variations
// Expected Version: 21
// Required Features: RECORDS, RECORD_PATTERNS, SWITCH_EXPRESSIONS, SWITCH_MULTIPLE_LABELS, SWITCH_NULL_DEFAULT, SWITCH_PATTERN_MATCHING, YIELD
public class SwitchEdgeCases_Java21 {

    public int basicSwitchExpression(String day) {
        return switch (day) {
            case "Monday", "Tuesday" -> 1;
            case "Saturday", "Sunday" -> 0;
            default -> -1;
        };
    }

    public String switchWithYield(int value) {
        return switch (value) {
            case 1 -> "one";
            case 2 -> "two";
            default -> {
                yield "number: " + value;
            }
        };
    }

    public String patternSwitch(Object obj) {
        return switch (obj) {
            case Integer i -> "int: " + i;
            case String s -> "string: " + s;
            case null -> "null";
            default -> "other";
        };
    }

    record Point(int x, int y) {}

    public String recordPatternSwitch(Object obj) {
        return switch (obj) {
            case Point(int x, int y) -> "Point at " + x + "," + y;
            case null, default -> "other";
        };
    }
}