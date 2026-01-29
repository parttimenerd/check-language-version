// Edge case: Generics variations (uses features up to Java 8)
// Expected Version: 8
// Required Features: COLLECTIONS_FRAMEWORK, DIAMOND_OPERATOR, GENERICS, INNER_CLASSES, STREAM_API
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

class GenericsEdgeCases_Java5 {

    // Generic class
    class Box<T> {
        private T value;

        public void set(T value) { this.value = value; }
        public T get() { return value; }
    }

    // Multiple type parameters
    class Pair<K, V> {
        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    // Bounded type parameters
    class NumberBox<T extends Number> {
        private T value;

        public double doubleValue() {
            return value.doubleValue();
        }
    }

    // Multiple bounds
    class MultiBound<T extends Comparable<T> & Cloneable> {
        private T value;
    }

    // Generic methods
    public <T> T identity(T value) {
        return value;
    }

    public <T extends Comparable<T>> T max(T a, T b) {
        return a.compareTo(b) > 0 ? a : b;
    }

    // Wildcards
    public void testWildcards() {
        // Unbounded wildcard
        List<?> unknown = new ArrayList<String>();

        // Upper bounded wildcard
        List<? extends Number> numbers = new ArrayList<Integer>();

        // Lower bounded wildcard
        List<? super Integer> integers = new ArrayList<Number>();
    }

    // Diamond operator (Java 7)
    public void testDiamondOperator() {
        // Without diamond (pre-Java 7)
        List<String> oldStyle = new ArrayList<String>();

        // With diamond (Java 7+)
        List<String> newStyle = new ArrayList<>();
        Map<String, List<Integer>> complex = new HashMap<>();

        // Diamond with anonymous class (Java 9)
        // Comparator<String> cmp = new Comparator<>() { ... };
    }

    // Generic constructors
    class GenericConstructor {
        public <T> GenericConstructor(T value) {
            System.out.println(value);
        }
    }

    // Recursive type bounds
    class RecursiveBound<T extends Comparable<T>> {
        public T findMax(List<T> items) {
            Stream<T> stream = items.stream();
            return stream.max(Comparator.naturalOrder()).orElse(null);
        }
    }
}