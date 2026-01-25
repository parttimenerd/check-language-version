// Test: Try with effectively final (Java 9)
// Expected Version: 9
// Required Features: TRY_WITH_EFFECTIVELY_FINAL
import java.io.*;
public class Tiny_TryEffectivelyFinal_Java9 {
    public void test() throws IOException {
        FileReader r = new FileReader("f.txt");
        try (r) { r.read(); }
    }
}