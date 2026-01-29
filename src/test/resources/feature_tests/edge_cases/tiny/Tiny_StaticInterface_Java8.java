// Test: Static interface methods (Java 8)
// Expected Version: 8
// Required Features: INNER_CLASSES, STATIC_INTERFACE_METHODS
class Tiny_StaticInterface_Java8 {
    interface I {
        static void method() { System.out.println("static"); }
    }
}