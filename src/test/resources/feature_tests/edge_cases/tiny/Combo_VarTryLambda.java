// Java 10 combination: Var + Try-with-resources + Lambda
// Test: Combination of var with try-with-resources and lambdas
// Expected Version: 10
// Required Features: VAR, TRY_WITH_RESOURCES, LAMBDAS
import java.io.*;
public class Combo_VarTryLambda_Java10 {
    public void test() throws IOException {
        var r = new FileInputStream("f.txt");
        try (r) {
            var processor = (Runnable) () -> System.out.println("processing");
            processor.run();
        }
    }
}