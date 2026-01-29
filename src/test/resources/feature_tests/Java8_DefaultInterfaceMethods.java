// Java 8 feature: Default methods in interfaces
// Expected Version: 8
// Required Features: ANNOTATIONS, DEFAULT_INTERFACE_METHODS, INNER_CLASSES
class Java8_DefaultInterfaceMethods {
    interface MyInterface {
        void abstractMethod();

        default void defaultMethod() {
            System.out.println("Default implementation");
        }

        default String anotherDefault(String input) {
            return "Processed: " + input;
        }
    }

    class Implementation implements MyInterface {
        @Override
        public void abstractMethod() {
            System.out.println("Abstract implementation");
        }
    }
}