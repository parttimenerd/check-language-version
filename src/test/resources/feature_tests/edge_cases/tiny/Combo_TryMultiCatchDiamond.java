// Java 7 combination: Try-with-resources + Multi-catch + Diamond
// Test: Combination of try-with-resources with multi-catch and diamond operator
// Expected Version: 7
// Required Features: DIAMOND_OPERATOR, GENERICS, IO_API, MULTI_CATCH, TRY_WITH_RESOURCES, COLLECTIONS_FRAMEWORK
import java.io.*;
class Combo_TryMultiCatchDiamond {
    public void test() {
        java.util.List<String> list = new java.util.ArrayList<>();
        try (FileInputStream fis = new FileInputStream("f.txt")) {
            fis.read();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }
}