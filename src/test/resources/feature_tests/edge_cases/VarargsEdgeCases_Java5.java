// Edge case: Varargs in different contexts
// Expected Version: 5
// Required Features: ALPHA3_ARRAY_SYNTAX, ANNOTATIONS, COLLECTIONS_FRAMEWORK, FOR_EACH, GENERICS, INNER_CLASSES, VARARGS
import java.util.*;

class VarargsEdgeCases_Java5 {

    // Basic varargs method
    public void printAll(String... items) {
        for (String item : items) {
            System.out.println(item);
        }
    }

    // Varargs with other parameters
    public void printWithPrefix(String prefix, String... items) {
        for (String item : items) {
            System.out.println(prefix + item);
        }
    }

    // Varargs with primitive types
    public int sum(int... numbers) {
        int total = 0;
        for (int n : numbers) {
            total += n;
        }
        return total;
    }

    // Generic varargs
    @SafeVarargs
    public final <T> List<T> asList(T... items) {
        List<T> list = new ArrayList<T>();
        for (T item : items) {
            list.add(item);
        }
        return list;
    }

    // Calling varargs methods
    public void testVarargsCalls() {
        // No arguments
        printAll();

        // Single argument
        printAll("hello");

        // Multiple arguments
        printAll("a", "b", "c");

        // Array argument
        String[] array = {"x", "y", "z"};
        printAll(array);

        // Mixed calls
        printWithPrefix(">> ", "item1", "item2");

        // Primitive varargs
        int total = sum(1, 2, 3, 4, 5);
    }

    // Varargs in constructor
    static class VarargsConstructor {
        private final String[] values;

        public VarargsConstructor(String... values) {
            this.values = values;
        }
    }

    // Varargs with Object
    public void printObjects(Object... objects) {
        for (Object obj : objects) {
            System.out.println(obj);
        }
    }

    // Using printf (varargs in JDK)
    public void testPrintf() {
        System.out.printf("Name: %s, Age: %d%n", "John", 30);
        String formatted = String.format("Value: %.2f", 3.14159);
    }
}