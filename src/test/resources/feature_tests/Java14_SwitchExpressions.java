// Java 14 feature: Switch expressions (JEP 361)
// Expected Version: 14
// Required Features: SWITCH_EXPRESSIONS
public class Java14_SwitchExpressions {
    public String getDayType(String day) {
        // Switch expression with arrow syntax
        return switch (day) {
            case "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" -> "Weekday";
            case "Saturday", "Sunday" -> "Weekend";
            default -> "Invalid day";
        };
    }

    public int getDayNumber(String day) {
        // Switch expression returning a value
        int num = switch (day) {
            case "Monday" -> 1;
            case "Tuesday" -> 2;
            case "Wednesday" -> 3;
            case "Thursday" -> 4;
            case "Friday" -> 5;
            case "Saturday" -> 6;
            case "Sunday" -> 7;
            default -> 0;
        };
        return num;
    }
}