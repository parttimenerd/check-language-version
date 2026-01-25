// Java 5 edge case: Deeply nested generics with wildcards
// Test: Testing deeply nested generic types with bounded wildcards
// Expected Version: 5
// Required Features: COLLECTIONS_FRAMEWORK, GENERICS
import java.util.*;
public class Edge_DeeplyNestedGenerics {
    Map<String, List<Set<Map<Integer, String>>>> complex;
    List<? extends Number> wildcardExtends;
    List<? super Integer> wildcardSuper;
}