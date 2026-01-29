// Java 8 edge case: Default method in interface
// Test: default keyword in interface (minimal)
// Expected Version: 8
// Required Features: DEFAULT_INTERFACE_METHODS, INNER_CLASSES
class Edge_DefaultInterface {
    interface I {
        default void method() { System.out.println("default"); }
    }
}