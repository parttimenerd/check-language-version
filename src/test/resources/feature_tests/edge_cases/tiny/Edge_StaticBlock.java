// Java 4 edge case: Assert in static block
// Test: Assert statement hidden in static initializer
// Expected Version: 4
// Required Features: ASSERT
class Edge_StaticBlock {
    static { assert true; }
}