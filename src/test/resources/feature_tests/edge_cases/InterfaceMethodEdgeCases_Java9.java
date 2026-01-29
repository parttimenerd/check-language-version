// Edge case: Interface method variations
// Expected Version: 9
// Required Features: DATE_TIME_API, DEFAULT_INTERFACE_METHODS, INNER_CLASSES, PRIVATE_INTERFACE_METHODS, STATIC_INTERFACE_METHODS
class InterfaceMethodEdgeCases_Java9 {

    // Interface with default methods (Java 8)
    interface Greeting {
        String greet(String name);

        // Default method
        default String greetWithPrefix(String name) {
            return "Hello, " + greet(name);
        }

        // Multiple default methods
        default String greetFormal(String name) {
            return "Dear " + greet(name);
        }
    }

    // Interface with static methods (Java 8)
    interface MathOperations {
        int calculate(int a, int b);

        // Static method
        static int add(int a, int b) {
            return a + b;
        }

        static int multiply(int a, int b) {
            return a * b;
        }
    }

    // Interface with private methods (Java 9)
    interface Logger {
        void log(String message);

        // Private method for code reuse
        private String formatMessage(String message) {
            return "[" + java.time.LocalDateTime.now() + "] " + message;
        }

        // Private static method
        private static String getPrefix() {
            return "LOG: ";
        }

        // Default method using private method
        default void logInfo(String message) {
            log(getPrefix() + formatMessage(message));
        }

        default void logError(String message) {
            log("ERROR: " + formatMessage(message));
        }
    }

    // Combining all method types
    interface CompleteInterface {
        // Abstract method
        void abstractMethod();

        // Default method (Java 8)
        default void defaultMethod() {
            helperMethod();
        }

        // Static method (Java 8)
        static void staticMethod() {
            System.out.println("Static method");
        }

        // Private method (Java 9)
        private void helperMethod() {
            System.out.println("Private helper");
        }

        // Private static method (Java 9)
        private static void staticHelper() {
            System.out.println("Private static helper");
        }
    }
}