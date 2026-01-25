// Java 8 feature: Static methods in interfaces
// Expected Version: 8
// Required Features: STATIC_INTERFACE_METHODS
public class Java8_StaticInterfaceMethods {
    interface Calculator {
        // Static method in interface (Java 8)
        static int add(int a, int b) {
            return a + b;
        }

        static int multiply(int a, int b) {
            return a * b;
        }

        // Abstract method
        int calculate(int value);

        // Default method
        default int doubleValue(int value) {
            return value * 2;
        }
    }

    public void testStaticInterfaceMethods() {
        // Call static method directly on interface
        int sum = Calculator.add(5, 3);
        int product = Calculator.multiply(4, 7);

        System.out.println("Sum: " + sum);
        System.out.println("Product: " + product);
    }
}