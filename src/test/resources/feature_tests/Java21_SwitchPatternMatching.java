// Java 21 feature: Pattern matching in switch
// Expected Version: 21
// Required Features: SWITCH_PATTERN_MATCHING
public class Java21_SwitchPatternMatching {
    sealed interface Shape permits Circle, Rectangle, Triangle {}
    record Circle(double radius) implements Shape {}
    record Rectangle(double w, double h) implements Shape {}
    record Triangle(double base, double height) implements Shape {}

    public double area(Shape shape) {
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Rectangle r -> r.w() * r.h();
            case Triangle t -> 0.5 * t.base() * t.height();
        };
    }

    public String describe(Object obj) {
        return switch (obj) {
            case Integer i -> "Integer: " + i;
            case String s when s.length() > 10 -> "Long string: " + s.substring(0, 10) + "...";
            case String s -> "String: " + s;
            case null -> "null value";
            default -> "Unknown: " + obj.getClass();
        };
    }

    public String categorize(Integer i) {
        return switch (i) {
            case Integer n when n < 0 -> "negative";
            case Integer n when n == 0 -> "zero";
            case Integer n when n > 0 && n <= 10 -> "small positive";
            case Integer n -> "large positive";
        };
    }
}