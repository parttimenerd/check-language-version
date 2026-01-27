// Java 16 edge case: Minimal record
// Test: Record that looks like it could be a simple class
// Expected Version: 16
// Required Features: RECORDS
public class Edge_RecordMinimal {
    record Point(int x, int y) {}
}