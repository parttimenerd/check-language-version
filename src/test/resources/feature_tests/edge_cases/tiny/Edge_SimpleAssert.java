// Java 4 edge case: Assert without obvious modern features
// Test: Simple assert that might be confused with validation
// Expected Version: 4
// Required Features: ASSERT
class Edge_SimpleAssert {
    void test(int x) {
        assert x > 0;
    }
}