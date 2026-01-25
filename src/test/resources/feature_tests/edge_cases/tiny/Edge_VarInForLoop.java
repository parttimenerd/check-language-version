// Java 10 edge case: Var in for loop
// Test: Testing var keyword in traditional and enhanced for loops
// Expected Version: 10
// Required Features: VAR
public class Edge_VarInForLoop_Java10 {
    public void test() {
        for (var i = 0; i < 10; i++) {
            System.out.println(i);
        }

        var list = java.util.List.of(1, 2, 3);
        for (var item : list) {
            System.out.println(item);
        }
    }
}