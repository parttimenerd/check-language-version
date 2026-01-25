// Java 1.4 edge case: Assert with message
// Test: Testing assert statements with and without messages
// Expected Version: 4
// Required Features: ASSERT
public class Edge_AssertWithMessage_Java4 {
    void test(int x) {
        assert x > 0 : "x must be positive";
        assert x < 100;
    }
}