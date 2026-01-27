// Java 7 edge case: Binary literal without underscores
// Test: Binary literal alone (no other Java 7 features)
// Expected Version: 7
// Required Features: BINARY_LITERALS
public class Edge_BinaryLiteral {
    int binary = 0b1010_1010;
}