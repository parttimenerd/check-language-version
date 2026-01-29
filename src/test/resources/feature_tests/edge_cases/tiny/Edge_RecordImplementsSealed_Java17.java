// Java 17 edge case: Record implementing sealed interface
// Test: Testing records implementing sealed interfaces
// Expected Version: 17
// Required Features: INNER_CLASSES, RECORDS, SEALED_CLASSES
class Edge_RecordImplementsSealed_Java17 {
    sealed interface Shape permits Circle, Square {}
    record Circle(double radius) implements Shape {}
    record Square(double side) implements Shape {}
}