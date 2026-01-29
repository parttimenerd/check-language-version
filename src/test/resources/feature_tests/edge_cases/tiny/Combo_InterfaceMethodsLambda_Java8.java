// Java 8 combination: Default and static interface methods + Lambda
// Test: Combination of default and static interface methods with lambdas
// Expected Version: 8
// Required Features: DEFAULT_INTERFACE_METHODS, INNER_CLASSES, LAMBDAS, STATIC_INTERFACE_METHODS
class Combo_InterfaceMethodsLambda_Java8 {
    interface MyInterface {
        default void defaultMethod() {
            run(() -> System.out.println("default"));
        }
        static void staticMethod() {
            System.out.println("static");
        }
        void run(Runnable r);
    }
}