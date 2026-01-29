// Java 10 edge case: Var in all supported contexts
// Test: Testing var keyword in all supported contexts (local vars, for loops, try-with-resources)
// Expected Version: 10
// Required Features: COLLECTIONS_FRAMEWORK, FOR_EACH, GENERICS, IO_API, TRY_WITH_RESOURCES, VAR
import java.util.*;
class Edge_VarAllContexts {
    void test() {
        var x = 10;
        var s = "hello";
        var list = new ArrayList<String>();
        for (var item : list) {}
        for (var i = 0; i < 10; i++) {}
        try (var r = new java.io.StringReader("")) {}
    }
}