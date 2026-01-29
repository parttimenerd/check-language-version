// Java 10 combination: Var + Try-with-resources + Lambda
// Test: Combination of var with try-with-resources and lambdas
// Expected Version: 10
// Required Features: IO_API, LAMBDAS, TRY_WITH_EFFECTIVELY_FINAL, TRY_WITH_RESOURCES, VAR
import java.io.*;
class Combo_VarTryLambda {
    public void test() throws IOException {
        var r = new FileInputStream("f.txt");
        try (r) {
            var processor = (Runnable) () -> System.out.println("processing");
            processor.run();
        }
    }
}