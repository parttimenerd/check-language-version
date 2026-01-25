// Test: Private interface methods (Java 9)
// Expected Version: 9
// Required Features: PRIVATE_INTERFACE_METHODS
public class Tiny_PrivateInterface_Java9 {
    interface I {
        default void pub() { helper(); }
        private void helper() { System.out.println("private"); }
    }
}