// Java 16 edge case: Record with generic type parameters
// Test: Testing records with generic type parameters
// Expected Version: 16
// Required Features: DIAMOND_OPERATOR, GENERICS, RECORDS
public class Edge_GenericRecords_Java16 {
    record Pair<T, U>(T first, U second) {}

    void test() {
        Pair<String, Integer> p = new Pair<>("hello", 42);
    }
}