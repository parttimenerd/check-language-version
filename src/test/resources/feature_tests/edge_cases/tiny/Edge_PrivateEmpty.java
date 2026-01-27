// Java 9 edge case: Empty private interface method
// Test: Minimal private method in interface
// Expected Version: 9
// Required Features: PRIVATE_INTERFACE_METHODS, DEFAULT_INTERFACE_METHODS
interface Edge_PrivateEmpty {
    private void h() {}
    default void m() { h(); }
}
public class Tiny_PrivateIface_Java9 implements Edge_PrivateEmpty {}