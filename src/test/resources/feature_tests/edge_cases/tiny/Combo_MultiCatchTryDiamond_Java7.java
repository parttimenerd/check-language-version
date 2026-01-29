// Java 7 combination: Multi-catch + Try-with-resources + Diamond
// Test: Combination of multi-catch with try-with-resources and diamond operator
// Expected Version: 7
// Required Features: COLLECTIONS_FRAMEWORK, DIAMOND_OPERATOR, GENERICS, IO_API, MULTI_CATCH, TRY_WITH_RESOURCES
import java.io.*;
import java.util.*;
class Combo_MultiCatchTryDiamond_Java7 {
    void test() {
        try (FileReader fr = new FileReader("test.txt")) {
            List<String> list = new ArrayList<>();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }
}