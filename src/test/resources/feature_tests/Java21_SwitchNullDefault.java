// Java 21 feature: Switch case null, default
// Expected Version: 21
// Required Features: SWITCH_EXPRESSIONS, SWITCH_NULL_DEFAULT
class Java21_SwitchNullDefault {
    public String process(String input) {
        return switch (input) {
            case "A" -> "Option A";
            case "B" -> "Option B";
            case null, default -> "Unknown or null";
        };
    }

    public int getValue(Integer num) {
        return switch (num) {
            case 1 -> 100;
            case 2 -> 200;
            case null, default -> 0;
        };
    }
}