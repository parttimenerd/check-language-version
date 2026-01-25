// Java 11 combination: Var + Try-with-resources + Var in lambda (Advanced)
// Test: Advanced combination of var with try-with-resources and var in lambda parameters
// Expected Version: 11
// Required Features: IO_API, LAMBDAS, TRY_WITH_RESOURCES, VAR, VAR_IN_LAMBDA
import java.io.*;
public class Combo_VarTryLambda_Java11_Advanced {
    void test() throws IOException {
        try (var reader = new BufferedReader(new FileReader("test.txt"))) {
            reader.lines().forEach((var line) -> System.out.println(line));
        }
    }
}