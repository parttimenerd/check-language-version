// Test: Static interface methods (Java 8)
// Expected Version: 8
// Required Features: STATIC_INTERFACE_METHODS
public class Tiny_StaticInterface_Java8 {
    interface I {
        static void method() { System.out.println("static"); }
    }
}