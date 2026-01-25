// Java 9 edge case: Interface with all method types
// Test: Testing interface with abstract, default, static, and private methods
// Expected Version: 9
// Required Features: DEFAULT_INTERFACE_METHODS, INNER_CLASSES, PRIVATE_INTERFACE_METHODS, STATIC_INTERFACE_METHODS
public class Edge_InterfaceAllMethods {
    interface Complete {
        void abstractMethod();

        default void defaultMethod() {
            privateHelper();
        }

        static void staticMethod() {
            privateStaticHelper();
        }

        private void privateHelper() {
            System.out.println("private instance");
        }

        private static void privateStaticHelper() {
            System.out.println("private static");
        }
    }
}