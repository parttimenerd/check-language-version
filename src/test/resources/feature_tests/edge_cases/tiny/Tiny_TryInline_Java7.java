// Tiny: Try resource inline (Java 7)
// Expected Version: 7
// Required Features: TRY_WITH_RESOURCES

public class Tiny_TryInline_Java7 {
    void test() throws Exception {
        try (java.io.Reader r = new java.io.StringReader("")) {}
    }
}