// Edge case: Sealed class variations (uses Java 21 switch pattern matching)
// Expected Version: 21
// Required Features: SEALED_CLASSES, SWITCH_PATTERN_MATCHING
public class SealedClassEdgeCases_Java17 {

    sealed class Shape permits Circle, Rectangle {}

    final class Circle extends Shape {
        double radius;
    }

    final class Rectangle extends Shape {
        double width, height;
    }

    sealed interface Vehicle permits Car, Truck {}

    final class Car implements Vehicle {}
    final class Truck implements Vehicle {}

    sealed interface Expression permits Constant, Add {}

    record Constant(int value) implements Expression {}
    record Add(Expression left, Expression right) implements Expression {}

    public String describeShape(Shape shape) {
        return switch (shape) {
            case Circle c -> "Circle";
            case Rectangle r -> "Rectangle";
        };
    }
}