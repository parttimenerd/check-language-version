// Java 2 edge case: strictfp modifier
// Test: strictfp keyword that's easy to miss
// Expected Version: 2
// Required Features: STRICTFP
public strictfp class Edge_StrictfpClass {
    double calc() { return 1.0 / 3.0; }
}