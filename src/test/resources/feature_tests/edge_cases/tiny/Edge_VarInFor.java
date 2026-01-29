// Java 10 edge case: var only in for loop
// Test: var used in traditional for loop
// Expected Version: 10
// Required Features: VAR
class Edge_VarInFor {
    void test() { for (var i = 0; i < 1; i++) {} }
}