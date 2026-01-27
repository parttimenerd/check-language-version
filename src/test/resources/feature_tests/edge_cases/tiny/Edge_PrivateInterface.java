// Java 9 edge case: Private method in interface
// Test: private keyword in interface (easy to miss)
// Expected Version: 9
// Required Features: PRIVATE_INTERFACE_METHODS, DEFAULT_INTERFACE_METHODS
public class Edge_PrivateInterface {
    interface I {
        default void pub() { helper(); }
        private void helper() { System.out.println("private"); }
    }
}