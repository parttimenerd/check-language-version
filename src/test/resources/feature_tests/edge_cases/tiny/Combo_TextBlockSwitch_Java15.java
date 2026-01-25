// Java 15 combination: Text blocks + Switch expressions
// Test: Combination of text blocks with switch expressions
// Expected Version: 15
// Required Features: TEXT_BLOCKS, SWITCH_EXPRESSIONS
public class Combo_TextBlockSwitch_Java15 {
    String test(int x) {
        return switch (x) {
            case 1 -> """
                First
                Line
                """;
            default -> """
                Default
                """;
        };
    }
}