// Java 7 edge case: Hex literal with underscores
// Test: Hex literal with underscores (easy to miss)
// Expected Version: 7
// Required Features: UNDERSCORES_IN_LITERALS
public class Edge_HexUnderscore {
    int hex = 0xFF_FF_FF_FF;
}