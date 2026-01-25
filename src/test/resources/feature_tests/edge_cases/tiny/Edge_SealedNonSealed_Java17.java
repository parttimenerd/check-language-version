// Java 17 edge case: Sealed class hierarchy with non-sealed subclass
// Test: Testing sealed classes with final and non-sealed permitted subclasses
// Expected Version: 17
// Required Features: SEALED_CLASSES
public class Edge_SealedNonSealed_Java17 {
    sealed interface Animal permits Dog, Cat {}
    final class Dog implements Animal {}
    non-sealed class Cat implements Animal {}
}