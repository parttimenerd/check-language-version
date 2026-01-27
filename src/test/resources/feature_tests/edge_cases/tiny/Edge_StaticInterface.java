// Java 8 edge case: Static method in interface
// Test: static keyword in interface method
// Expected Version: 8
// Required Features: STATIC_INTERFACE_METHODS
public class Edge_StaticInterface {
    interface I { static int get() { return 1; } }
    int x = I.get();
}