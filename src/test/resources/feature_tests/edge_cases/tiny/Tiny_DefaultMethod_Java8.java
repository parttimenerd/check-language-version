// Test: Default interface methods (Java 8)
// Expected Version: 8
// Required Features: DEFAULT_INTERFACE_METHODS, INNER_CLASSES
public class Tiny_DefaultMethod_Java8 {
    interface I {
        default void method() { System.out.println("default"); }
    }
}