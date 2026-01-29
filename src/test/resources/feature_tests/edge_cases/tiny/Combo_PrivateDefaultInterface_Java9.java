// Java 9 combination: Private interface methods + Default methods
// Test: Combination of private interface methods with default methods
// Expected Version: 9
// Required Features: DEFAULT_INTERFACE_METHODS, INNER_CLASSES, PRIVATE_INTERFACE_METHODS
class Combo_PrivateDefaultInterface_Java9 {
    interface MyInterface {
        default void publicMethod() {
            privateHelper();
        }
        private void privateHelper() {
            System.out.println("private");
        }
    }
}