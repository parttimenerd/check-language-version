// Tiny: Sealed in inner (Java 17)
// Expected Version: 17
// Required Features: SEALED_CLASSES

public class Tiny_SealedInner_Java17 {
    sealed interface I permits A {}
    final class A implements I {}
}