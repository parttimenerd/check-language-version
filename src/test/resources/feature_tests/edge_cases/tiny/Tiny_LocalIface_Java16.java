// Tiny: Local interface (Java 16)
// Expected Version: 16
// Required Features: INNER_CLASSES, LAMBDAS, LOCAL_INTERFACES

class Tiny_LocalIface_Java16 {
    void test() {
        interface Local { void run(); }
        Local l = () -> {};
    }
}