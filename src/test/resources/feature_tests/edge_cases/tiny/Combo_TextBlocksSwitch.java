// Java 15 combination: Text blocks + Switch expressions
// Test: Combination of text blocks with switch expressions
// Expected Version: 15
// Required Features: TEXT_BLOCKS, SWITCH_EXPRESSIONS
class Combo_TextBlocksSwitch {
    public String test(String type) {
        return switch (type) {
            case "json" -> """
                {"key": "value"}
                """;
            case "html" -> """
                <html><body>Test</body></html>
                """;
            default -> "plain text";
        };
    }
}