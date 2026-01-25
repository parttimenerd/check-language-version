// Tiny: Default method empty (Java 8)
// Expected Version: 8
// Required Features: DEFAULT_INTERFACE_METHODS

interface I { default void m() {} }

public class Tiny_DefaultEmpty_Java8 implements I {}