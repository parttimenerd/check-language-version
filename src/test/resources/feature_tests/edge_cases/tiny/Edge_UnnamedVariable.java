// Java 22 edge case: Unnamed variable in for-each
// Test: Single underscore as variable name
// Expected Version: 22
// Required Features: FOR_EACH, UNNAMED_VARIABLES
public class Edge_UnnamedVariable {
    void test() { for (int _ : new int[]{1}) {} }
}