// Java 17 feature: Sealed classes
// Test: Sealed classes (Java 17)
// Expected Version: 17
// Required Features: INNER_CLASSES, PATTERN_MATCHING_INSTANCEOF, RECORDS, SEALED_CLASSES

public class Java17_SealedClasses {

    // Sealed interface
    sealed interface Shape permits Circle, Rectangle, Triangle {}

    // Permitted implementations
    record Circle(double radius) implements Shape {}
    record Rectangle(double width, double height) implements Shape {}
    final class Triangle implements Shape {
        private final double base, height;
        Triangle(double base, double height) {
            this.base = base;
            this.height = height;
        }
        double getBase() { return base; }
        double getHeight() { return height; }
    }

    // Sealed class
    sealed class Animal permits Dog, Cat {}
    final class Dog extends Animal {}
    non-sealed class Cat extends Animal {}  // Can be extended further

    // Use instanceof pattern matching (Java 16) instead of switch patterns (Java 21)
    public double area(Shape shape) {
        if (shape instanceof Circle c) {
            return Math.PI * c.radius() * c.radius();
        } else if (shape instanceof Rectangle r) {
            return r.width() * r.height();
        } else if (shape instanceof Triangle t) {
            return 0.5 * t.getBase() * t.getHeight();
        }
        return 0;
    }
}