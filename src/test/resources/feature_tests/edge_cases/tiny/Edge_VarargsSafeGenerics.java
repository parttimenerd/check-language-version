// Java 5 edge case: Varargs with @SafeVarargs and generics
// Test: Testing varargs with @SafeVarargs annotation and generic types
// Expected Version: 5
// Required Features: ANNOTATIONS, AUTOBOXING, COLLECTIONS_FRAMEWORK, GENERICS, VARARGS
import java.util.*;

class Edge_VarargsSafeGenerics {
    @SafeVarargs
    final <T> List<T> asList(T... elements) {
        return Arrays.asList(elements);
    }

    void test() {
        List<String> strings = asList("a", "b", "c");
        List<Integer> ints = asList(1, 2, 3);
    }
}