// Java 22 feature: Unnamed variables (_)
// Expected Version: 22
// Required Features: COLLECTIONS_FRAMEWORK, COLLECTION_FACTORY_METHODS, DIAMOND_OPERATOR, FOR_EACH, GENERICS, LAMBDAS, PATTERN_MATCHING_INSTANCEOF, RECORDS, RECORD_PATTERNS, UNNAMED_VARIABLES
import java.util.*;

class Java22_UnnamedVariables {
    record Point(int x, int y) {}

    public void method() {
        // Unnamed variable in enhanced for (when value not needed)
        int count = 0;
        for (String _ : List.of("a", "b", "c")) {
            count++;
        }

        // Unnamed variable in catch
        try {
            throw new Exception("test");
        } catch (Exception _) {
            System.out.println("Exception occurred");
        }

        // Unnamed variable in lambda
        Map<String, Integer> map = new HashMap<>();
        map.computeIfAbsent("key", _ -> 42);

        // Unnamed pattern variable
        if (new Point(1, 2) instanceof Point(int x, int _)) {
            System.out.println("x = " + x);
        }
    }
}