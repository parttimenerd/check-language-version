// Java 16 edge case: Record as top-level declaration
// Test: Record used as type (requires Java 16)
// Expected Version: 16
// Required Features: RECORDS
record Edge_RecordParam(int x) {}
class Tiny_RecordParam_Java16 {
    void test(Edge_RecordParam r) {}
}