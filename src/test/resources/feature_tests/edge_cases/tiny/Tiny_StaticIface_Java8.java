// Tiny: Static interface constant (Java 8)
// Expected Version: 8
// Required Features: STATIC_INTERFACE_METHODS

interface I { static int get() { return 1; } }

class Tiny_StaticIface_Java8 {
    int x = I.get();
}