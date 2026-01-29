// Edge case: For-each loop variations
// Expected Version: 5
// Required Features: ALPHA3_ARRAY_SYNTAX, AUTOBOXING, COLLECTIONS_FRAMEWORK, FOR_EACH, GENERICS, VARARGS
import java.util.*;

class ForEachEdgeCases_Java5 {

    // Java 5: Basic for-each with array
    public void testForEachArray() {
        int[] numbers = {1, 2, 3, 4, 5};
        for (int n : numbers) {
            System.out.println(n);
        }

        String[] words = {"hello", "world"};
        for (String word : words) {
            System.out.println(word);
        }
    }

    // Java 5: For-each with collections
    public void testForEachCollection() {
        List<String> list = new ArrayList<String>();
        list.add("a");
        list.add("b");
        list.add("c");

        for (String item : list) {
            System.out.println(item);
        }

        Set<Integer> set = new HashSet<Integer>();
        for (Integer num : set) {
            System.out.println(num);
        }
    }

    // Java 5: For-each with Map.entrySet()
    public void testForEachMap() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("one", 1);
        map.put("two", 2);

        // Iterate over entries
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }

        // Iterate over keys
        for (String key : map.keySet()) {
            System.out.println(key);
        }

        // Iterate over values
        for (Integer value : map.values()) {
            System.out.println(value);
        }
    }

    // Java 5: Nested for-each
    public void testNestedForEach() {
        int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};

        for (int[] row : matrix) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }

        List<List<String>> nested = new ArrayList<List<String>>();
        for (List<String> inner : nested) {
            for (String item : inner) {
                System.out.println(item);
            }
        }
    }

    // Java 5: For-each with varargs
    public void testForEachVarargs(String... args) {
        for (String arg : args) {
            System.out.println(arg);
        }
    }

    // Edge case: For-each with Iterable
    public void testForEachIterable(Iterable<String> iterable) {
        for (String item : iterable) {
            System.out.println(item);
        }
    }

    // Edge case: For-each vs traditional for
    public void testComparison() {
        String[] array = {"a", "b", "c"};

        // Traditional for loop (Java 1)
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }

        // Enhanced for loop (Java 5)
        for (String s : array) {
            System.out.println(s);
        }
    }

    // Edge case: For-each with final variable
    public void testFinalVariable() {
        List<String> list = Arrays.asList("a", "b", "c");

        for (final String item : list) {
            // item is final - cannot reassign
            System.out.println(item);
        }
    }
}