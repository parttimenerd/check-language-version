// Tiny: Sealed in inner (Java 17)
// Expected Version: 17
// Required Features: INNER_CLASSES, SEALED_CLASSES

class Tiny_SealedInner_Java17 {
    sealed interface I permits A {}
    final class A implements I {}
}