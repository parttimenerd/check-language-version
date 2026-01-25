// Edge case: All Java 7 syntax features in one file
// Expected Version: 7
// Required Features: BINARY_LITERALS, COLLECTIONS_FRAMEWORK, DIAMOND_OPERATOR, GENERICS, IO_API, MULTI_CATCH, NIO2, STRINGS_IN_SWITCH, TRY_WITH_RESOURCES, UNDERSCORES_IN_LITERALS
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AllJava7Features {

    public void testDiamondOperator() {
        List<String> list = new ArrayList<>();
        Map<String, List<Integer>> complex = new HashMap<>();
    }

    public void testTryWithResources() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("test.txt"))) {
            String line = reader.readLine();
        }
    }

    public void testMultiCatch() {
        try {
            Class.forName("test");
        } catch (ClassNotFoundException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void testStringsInSwitch(String day) {
        switch (day) {
            case "Monday":
                System.out.println("Start");
                break;
            default:
                System.out.println("Other");
        }
    }

    public void testBinaryAndUnderscoreLiterals() {
        int binary = 0b1010_1010;
        long creditCard = 1234_5678_9012_3456L;
    }

    public void testNio2() throws IOException {
        Path path = Paths.get("/tmp", "test.txt");
        boolean exists = Files.exists(path);
    }
}