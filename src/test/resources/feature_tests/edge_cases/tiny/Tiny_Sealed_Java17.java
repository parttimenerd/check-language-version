// Test: Sealed classes (Java 17)
// Expected Version: 17
// Required Features: INNER_CLASSES, SEALED_CLASSES
public class Tiny_Sealed_Java17 {
    sealed interface Shape permits Circle, Square {}
    final class Circle implements Shape {}
    final class Square implements Shape {}
}