// Java 5 edge case: Static nested class with generics
// Test: Testing static nested classes with generic type parameters
// Expected Version: 5
// Required Features: GENERICS, INNER_CLASSES
public class Edge_StaticNestedGenerics {
    static class Nested<T> {
        T value;
        Nested(T value) {
            this.value = value;
        }
    }
}