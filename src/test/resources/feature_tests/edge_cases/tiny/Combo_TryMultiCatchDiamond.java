// Java 7 combination: Try-with-resources + Multi-catch + Diamond
// Test: Combination of try-with-resources with multi-catch and diamond operator
// Expected Version: 7
// Required Features: TRY_WITH_RESOURCES, MULTI_CATCH, DIAMOND_OPERATOR
import java.io.*;
public class Combo_TryMultiCatchDiamond_Java7 {
    public void test() {
        java.util.List<String> list = new java.util.ArrayList<>();
        try (FileInputStream fis = new FileInputStream("f.txt")) {
            fis.read();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }
}