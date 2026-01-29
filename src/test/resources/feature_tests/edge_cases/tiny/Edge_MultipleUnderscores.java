// Edge: Multiple underscores in literal (Java 7)
// Expected Version: 7
// Required Features: BINARY_LITERALS, UNDERSCORES_IN_LITERALS
class Edge_MultipleUnderscores {
    long big = 1___000___000___000L;
    int hex = 0xFF___FF___FF;
    int binary = 0b1111____0000____1111____0000;
}