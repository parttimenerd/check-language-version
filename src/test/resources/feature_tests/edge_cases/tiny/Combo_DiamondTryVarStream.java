// Java 10 combination: Diamond + Try-with-resources + Var + Stream
// Test: Combination of diamond operator, try-with-resources, var, and streams
// Expected Version: 10
// Required Features: DIAMOND_OPERATOR, TRY_WITH_RESOURCES, VAR, STREAM_API
import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class Combo_DiamondTryVarStream_Java10 {
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