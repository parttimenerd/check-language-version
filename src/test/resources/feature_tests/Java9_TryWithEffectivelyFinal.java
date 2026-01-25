// Java 9 feature: Try-with-resources on effectively final variables
// Expected Version: 9
// Required Features: IO_API, TRY_WITH_EFFECTIVELY_FINAL, TRY_WITH_RESOURCES
import java.io.*;

public class Java9_TryWithEffectivelyFinal {
    public void testEffectivelyFinalResources() throws IOException {
        // Before Java 9: had to declare resource in try statement
        // try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) { ... }

        // Java 9: Can use effectively final variables
        BufferedReader reader1 = new BufferedReader(new StringReader("Hello"));
        BufferedReader reader2 = new BufferedReader(new StringReader("World"));

        // These are effectively final - not reassigned after initialization
        try (reader1; reader2) {
            System.out.println(reader1.readLine());
            System.out.println(reader2.readLine());
        }
    }

    public void anotherExample() throws IOException {
        final InputStream input = new ByteArrayInputStream("test".getBytes());

        // Final variable can also be used
        try (input) {
            int data = input.read();
            System.out.println("Read: " + data);
        }
    }
}