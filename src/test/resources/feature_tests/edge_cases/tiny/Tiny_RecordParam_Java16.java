// Tiny: Record in param (Java 16)
// Expected Version: 16
// Required Features: RECORDS

record R(int x) {}

class Tiny_RecordParam_Java16 {
    void test(R r) {}
}