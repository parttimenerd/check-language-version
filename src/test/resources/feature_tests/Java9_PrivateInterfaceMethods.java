// Java 9 feature: Private methods in interfaces
// Expected Version: 9
// Required Features: PRIVATE_INTERFACE_METHODS
public class Java9_PrivateInterfaceMethods {
    interface MyInterface {
        default void publicMethod() {
            privateHelper();
        }

        private void privateHelper() {
            System.out.println("Private helper method");
        }

        private static void privateStaticHelper() {
            System.out.println("Private static helper");
        }
    }
}