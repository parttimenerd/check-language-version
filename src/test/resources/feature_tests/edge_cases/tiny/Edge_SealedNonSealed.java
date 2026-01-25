// Edge: Sealed with non-sealed (Java 17)
// Expected Version: 17
// Required Features: SEALED_CLASSES
public class Edge_SealedNonSealed_Java17 {
    sealed interface Shape permits Circle, Polygon {}
    final class Circle implements Shape {}
    non-sealed class Polygon implements Shape {}
    class Triangle extends Polygon {}
}