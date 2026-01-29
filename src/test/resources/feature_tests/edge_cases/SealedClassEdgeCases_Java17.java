// Edge case: Sealed class variations (uses Java 21 switch pattern matching)
// Expected Version: 21
// Required Features: INNER_CLASSES, SEALED_CLASSES, SWITCH_EXPRESSIONS, SWITCH_PATTERN_MATCHING
class SealedClassEdgeCases_Java17 {

    sealed abstract class Shape permits Circle, Rectangle {}

    final class Circle extends Shape {
        double radius;
    }

    final class Rectangle extends Shape {
        double width, height;
    }

    public String describeShape(Shape shape) {
        return switch (shape) {
            case Circle c -> "Circle";
            case Rectangle r -> "Rectangle";
        };
    }
}