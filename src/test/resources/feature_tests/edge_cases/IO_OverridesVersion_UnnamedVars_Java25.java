// Edge case: Copy of Java22_UnnamedVariables + unqualified IO usage in a compact source file.
// Expected Version: 25
// Required Features: COLLECTIONS_FRAMEWORK, COLLECTION_FACTORY_METHODS, DIAMOND_OPERATOR, FOR_EACH, GENERICS, LAMBDAS, PATTERN_MATCHING_INSTANCEOF, RECORDS, RECORD_PATTERNS, UNNAMED_VARIABLES, IMPLICITLY_IMPORTED_IO_CLASS, IO_CLASS

import java.util.*;

class IO_OverridesVersion_UnnamedVars_Java25 {
    record Point(int x, int y) {}

    public void method() {
        int count = 0;
        for (String _ : List.of("a", "b", "c")) {
            count++;
        }

        Map<String, Integer> map = new HashMap<>();
        map.computeIfAbsent("key", _ -> 42);

        if (new Point(1, 2) instanceof Point(int x, int _)) {
            System.out.println("x = " + x);
        }
        IO.println();
    }
}