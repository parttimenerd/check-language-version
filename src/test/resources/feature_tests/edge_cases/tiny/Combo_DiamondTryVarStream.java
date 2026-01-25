// Java 10 combination: Diamond + Try-with-resources + Var + Stream
// Test: Combination of diamond operator, try-with-resources, var, and streams
// Expected Version: 10
// Required Features: COLLECTIONS_FRAMEWORK, DIAMOND_OPERATOR, GENERICS, IO_API, LAMBDAS, METHOD_REFERENCES, STREAM_API, TRY_WITH_RESOURCES, VAR
import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class Combo_DiamondTryVarStream {
    void test() throws IOException {
        // Diamond operator requires explicit type on left side
        List<String> list = new ArrayList<>();
        var x = "test";  // var usage
        try (var reader = new BufferedReader(new FileReader("test.txt"))) {
            // Explicit Stream type for detection
            Stream<String> lines = reader.lines()
                .filter(line -> !line.isEmpty());
            lines.forEach(System.out::println);
        }
    }
}