// Java 8 edge case: Empty default method
// Test: Minimal default method with no body
// Expected Version: 8
// Required Features: DEFAULT_INTERFACE_METHODS
interface Edge_DefaultEmpty { default void m() {} }
public class Tiny_DefaultEmpty_Java8 implements Edge_DefaultEmpty {}