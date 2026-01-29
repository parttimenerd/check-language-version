// Java 5 edge case: Varargs in constructor only
// Test: Varargs that's easy to miss
// Expected Version: 5
// Required Features: VARARGS
class Edge_VarargsConstructor {
    Edge_VarargsConstructor(int... x) {}
}