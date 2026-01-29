// Java 16 edge case: Generic record
// Test: Testing record with generic type parameters
// Expected Version: 16
// Required Features: DIAMOND_OPERATOR, GENERICS, RECORDS
class Edge_GenericRecord {
    record Pair<T, U>(T first, U second) {}

    Pair<String, Integer> pair = new Pair<>("test", 42);
}