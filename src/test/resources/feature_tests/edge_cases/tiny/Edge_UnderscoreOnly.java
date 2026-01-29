// Java 7 edge case: Underscores in literals only
// Test: Number with underscores, easy to miss
// Expected Version: 7
// Required Features: UNDERSCORES_IN_LITERALS
class Edge_UnderscoreOnly {
    long big = 1_000_000_000L;
}