// Java 17 edge case: non-sealed keyword
// Test: non-sealed in class hierarchy
// Expected Version: 17
// Required Features: SEALED_CLASSES
public class Edge_NonSealedClass {
    sealed interface Shape permits Circle, Polygon {}
    final class Circle implements Shape {}
    non-sealed class Polygon implements Shape {}
    class Triangle extends Polygon {}
}