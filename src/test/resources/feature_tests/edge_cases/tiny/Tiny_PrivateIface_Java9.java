// Tiny: Private interface helper (Java 9)
// Expected Version: 9
// Required Features: PRIVATE_INTERFACE_METHODS

interface I {
    private void h() {}
    default void m() { h(); }
}

public class Tiny_PrivateIface_Java9 implements I {}