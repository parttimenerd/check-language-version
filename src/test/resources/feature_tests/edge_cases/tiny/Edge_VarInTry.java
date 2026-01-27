// Java 10 edge case: var in try-with-resources
// Test: var keyword in resource declaration
// Expected Version: 10
// Required Features: VAR, TRY_WITH_RESOURCES, IO_API
public class Edge_VarInTry {
    void test() throws Exception {
        try (var r = new java.io.StringReader("")) {}
    }
}