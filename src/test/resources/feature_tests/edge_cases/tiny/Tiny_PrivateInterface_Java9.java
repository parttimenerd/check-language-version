// Test: Private interface methods (Java 9)
// Expected Version: 9
// Required Features: DEFAULT_INTERFACE_METHODS, INNER_CLASSES, PRIVATE_INTERFACE_METHODS
class Tiny_PrivateInterface_Java9 {
    interface I {
        default void pub() { helper(); }
        private void helper() { System.out.println("private"); }
    }
}