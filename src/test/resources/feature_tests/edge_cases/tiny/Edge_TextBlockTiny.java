// Java 15 edge case: Minimal text block
// Test: Tiny text block that's easy to miss the triple quotes
// Expected Version: 15
// Required Features: TEXT_BLOCKS
class Edge_TextBlockTiny {
    String s = """
        x""";
}