// Java 17 edge case: Minimal sealed interface
// Test: Sealed keyword easy to miss
// Expected Version: 17
// Required Features: SEALED_CLASSES
public class Edge_SealedMinimal {
    sealed interface I permits A {}
    final class A implements I {}
}