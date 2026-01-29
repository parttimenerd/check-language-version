// Java 16 edge case: Local interface in method
// Test: Interface declared inside method
// Expected Version: 16
// Required Features: LOCAL_INTERFACES, INNER_CLASSES
class Edge_LocalInterface {
    void test() {
        interface I { int X = 1; }
    }
}