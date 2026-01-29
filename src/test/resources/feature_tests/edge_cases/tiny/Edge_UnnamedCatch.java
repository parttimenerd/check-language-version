// Java 22 edge case: Unnamed variable in catch
// Test: Underscore in exception handler
// Expected Version: 22
// Required Features: UNNAMED_VARIABLES
class Edge_UnnamedCatch {
    void test() {
        try { throw new Exception(); }
        catch (Exception _) { }
    }
}