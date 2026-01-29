// Java 14 feature: Multiple labels in switch case
// Expected Version: 14
// Required Features: SWITCH_EXPRESSIONS, SWITCH_MULTIPLE_LABELS
class Java14_SwitchMultipleLabels {
    public String getQuarter(int month) {
        // Multiple labels per case
        return switch (month) {
            case 1, 2, 3 -> "Q1";
            case 4, 5, 6 -> "Q2";
            case 7, 8, 9 -> "Q3";
            case 10, 11, 12 -> "Q4";
            default -> "Invalid";
        };
    }
}