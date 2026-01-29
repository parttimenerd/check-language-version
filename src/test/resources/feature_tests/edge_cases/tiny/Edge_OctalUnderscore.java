// Java 7 edge case: Octal literal with underscores
// Test: Octal with underscores (subtle)
// Expected Version: 7
// Required Features: UNDERSCORES_IN_LITERALS
class Edge_OctalUnderscore {
    int octal = 07_7_7;
}