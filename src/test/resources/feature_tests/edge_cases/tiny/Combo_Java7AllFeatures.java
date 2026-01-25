// Java 7 combination: All major features
// Test: Combination of diamond, try-with-resources, multi-catch, binary literals, underscores, and strings in switch
// Expected Version: 7
// Required Features: BINARY_LITERALS, COLLECTIONS_FRAMEWORK, DIAMOND_OPERATOR, GENERICS, IO_API, MULTI_CATCH, STRINGS_IN_SWITCH, TRY_WITH_RESOURCES, UNDERSCORES_IN_LITERALS

import java.io.*;
import java.util.*;

public class Combo_Java7AllFeatures {
    void test(String type) throws IOException {
        List<String> list = new ArrayList<>();
        int binary = 0b1010_1010;

        try (FileReader fr = new FileReader("test.txt")) {
            switch (type) {
                case "binary": System.out.println(binary); break;
                case "list": System.out.println(list); break;
            }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }
}