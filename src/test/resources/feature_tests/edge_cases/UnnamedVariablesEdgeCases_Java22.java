// Edge case: Unnamed patterns and variables (Java 22)
// Expected Version: 22
// Required Features: ANNOTATIONS, AUTOBOXING, COLLECTIONS_FRAMEWORK, COLLECTION_FACTORY_METHODS, FOR_EACH, GENERICS, INNER_CLASSES, LAMBDAS, PATTERN_MATCHING_INSTANCEOF, RECORDS, RECORD_PATTERNS, SWITCH_EXPRESSIONS, SWITCH_NULL_DEFAULT, SWITCH_PATTERN_MATCHING, TRY_WITH_RESOURCES, UNNAMED_VARIABLES, VAR
import java.util.*;

public class UnnamedVariablesEdgeCases_Java22 {

    record Point(int x, int y) {}
    record Line(Point start, Point end) {}
    record Person(String name, int age, String address) {}

    // Java 22: Unnamed variable in enhanced for loop
    public void testUnnamedInForLoop() {
        List<String> items = List.of("a", "b", "c");
        int count = 0;

        // We only care about counting, not the value
        for (String _ : items) {
            count++;
        }
    }

    // Java 22: Unnamed variable in try-with-resources
    public void testUnnamedInTryWithResources() {
        // When we don't need to reference the resource
        try (var _ = new AutoCloseableResource()) {
            System.out.println("Using resource");
        }
    }

    // Java 22: Unnamed variable in catch
    public void testUnnamedInCatch() {
        try {
            riskyOperation();
        } catch (Exception _) {
            // We know it failed but don't need exception details
            System.out.println("Operation failed");
        }
    }

    // Java 22: Unnamed variable in lambda
    public void testUnnamedInLambda() {
        Map<String, Integer> map = Map.of("a", 1, "b", 2);

        // When we only need the value, not the key
        map.forEach((_, value) -> System.out.println(value));

        // When we only need the key, not the value
        map.forEach((key, _) -> System.out.println(key));
    }

    // Java 22: Unnamed pattern in instanceof
    public void testUnnamedPatternInInstanceof(Object obj) {
        // We only care about x coordinate
        if (obj instanceof Point(int x, _)) {
            System.out.println("x = " + x);
        }

        // We only care that it's a Point, not its values
        if (obj instanceof Point _) {
            System.out.println("It's a Point");
        }
    }

    // Java 22: Unnamed pattern in switch
    public String testUnnamedPatternInSwitch(Object obj) {
        return switch (obj) {
            case Point(int x, _) when x > 0 -> "positive x";
            case Point(_, int y) when y > 0 -> "positive y";
            case Point(_, _) -> "origin or negative";
            case Line(Point(int x, _), _) -> "line starting at x=" + x;
            case String _ -> "some string";
            case null, default -> "unknown";
        };
    }

    // Java 22: Nested unnamed patterns
    public void testNestedUnnamedPatterns(Object obj) {
        if (obj instanceof Line(Point(int x1, _), Point(int x2, _))) {
            System.out.println("Line from x=" + x1 + " to x=" + x2);
        }
    }

    // Java 22: Multiple unnamed in deconstruction
    public void testMultipleUnnamed() {
        Person person = new Person("John", 30, "123 Main St");

        // We only care about the name
        if (person instanceof Person(String name, _, _)) {
            System.out.println("Name: " + name);
        }
    }

    // Helper class for try-with-resources test
    static class AutoCloseableResource implements AutoCloseable {
        @Override
        public void close() {
            System.out.println("Closed");
        }
    }

    private void riskyOperation() throws Exception {
        throw new Exception("Risky!");
    }
}