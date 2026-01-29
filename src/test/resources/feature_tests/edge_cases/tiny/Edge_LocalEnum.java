// Java 16 edge case: Local enum in method
// Test: Enum declared inside method scope
// Expected Version: 16
// Required Features: LOCAL_ENUMS, ENUMS
class Edge_LocalEnum {
    void test() {
        enum E { A, B }
        E e = E.A;
    }
}