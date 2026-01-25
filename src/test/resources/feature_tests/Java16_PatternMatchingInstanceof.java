// Java 16 feature: Pattern matching for instanceof (JEP 394)
// Expected Version: 16
// Required Features: PATTERN_MATCHING_INSTANCEOF
public class Java16_PatternMatchingInstanceof {
    public void process(Object obj) {
        // Pattern matching with instanceof
        if (obj instanceof String s) {
            System.out.println("String of length: " + s.length());
        }

        if (obj instanceof Integer i && i > 0) {
            System.out.println("Positive integer: " + i);
        }

        // Negated pattern
        if (!(obj instanceof String s)) {
            System.out.println("Not a string");
        } else {
            System.out.println("String: " + s);
        }
    }

    public String describe(Object obj) {
        if (obj instanceof String s) {
            return "String: " + s;
        } else if (obj instanceof Integer i) {
            return "Integer: " + i;
        } else if (obj instanceof Double d) {
            return "Double: " + d;
        } else {
            return "Unknown type";
        }
    }
}