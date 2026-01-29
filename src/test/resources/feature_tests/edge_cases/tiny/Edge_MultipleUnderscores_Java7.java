// Java 7 edge case: Multiple underscores in all positions
// Test: Testing underscores in various numeric literal positions
// Expected Version: 7
// Required Features: BINARY_LITERALS, UNDERSCORES_IN_LITERALS
class Edge_MultipleUnderscores_Java7 {
    long creditCard = 1234_5678_9012_3456L;
    int binary = 0b1111_0000_1010_0101;
    int hex = 0xFF_EC_DE_5E;
    float f = 3.14_15_92_65F;
    long l = 1_000_000_000_000L;
}