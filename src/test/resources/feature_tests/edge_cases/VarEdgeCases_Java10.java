// Edge case: Var keyword in different contexts (includes Java 11 var in lambda)
// Expected Version: 11
// Required Features: VAR, VAR_IN_LAMBDA
import java.util.*;
import java.util.function.*;

public class VarEdgeCases_Java10 {

    public void testBasicVar() {
        // Basic var with primitives
        var i = 10;
        var d = 3.14;
        var s = "hello";
        var b = true;

        // Var with objects
        var list = new ArrayList<String>();
        var map = new HashMap<String, Integer>();
        var date = java.time.LocalDate.now();
    }

    public void testVarInForLoop() {
        // Var in traditional for loop
        for (var i = 0; i < 10; i++) {
            System.out.println(i);
        }

        // Var in enhanced for loop
        var items = List.of("a", "b", "c");
        for (var item : items) {
            System.out.println(item);
        }
    }

    public void testVarInTryWithResources() throws Exception {
        // Var in try-with-resources
        try (var reader = new java.io.BufferedReader(new java.io.FileReader("test.txt"))) {
            var line = reader.readLine();
        }
    }

    // Java 11: Var in lambda parameters
    public void testVarInLambda() {
        // Var in lambda (Java 11)
        BiFunction<String, String, String> concat = (var a, var b) -> a + b;

        // Var with annotations in lambda
        BiFunction<String, String, String> annotated =
            (@Deprecated var a, @Deprecated var b) -> a + b;

        // Mixed is not allowed: (var a, String b) -> ...
    }

    public void testVarWithDiamondOperator() {
        // Var with diamond - type inferred from left side
        var list = new ArrayList<String>();  // ArrayList<String>
        var map = new HashMap<String, List<Integer>>();  // HashMap<String, List<Integer>>
    }

    public void testVarLimitations() {
        // These are NOT allowed:
        // var x;  // No initializer
        // var x = null;  // null literal
        // var x = () -> {};  // lambda without target type
        // var x = { 1, 2, 3 };  // array initializer

        // But these ARE allowed:
        var arr = new int[] { 1, 2, 3 };  // explicit array type
        var lambda = (Runnable) () -> {};  // cast to provide type
    }
}