// Test: Try-with-resources (Java 7)
// Expected Version: 7
// Required Features: IO_API, TRY_WITH_RESOURCES
import java.io.*;
public class Tiny_TryWithResources_Java7 {
    public void test() throws IOException {
        try (FileInputStream fis = new FileInputStream("f.txt")) {
            fis.read();
        }
    }
}