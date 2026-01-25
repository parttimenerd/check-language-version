// Edge: Multiple underscores in literal (Java 7)
// Expected Version: 7
// Required Features: UNDERSCORES_IN_LITERALS
public class Edge_MultipleUnderscores_Java7 {
    long big = 1___000___000___000L;
    int hex = 0xFF___FF___FF;
    int binary = 0b1111____0000____1111____0000;
}