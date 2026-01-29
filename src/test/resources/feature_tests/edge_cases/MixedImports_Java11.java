// Edge case: Mix of explicit imports and wildcard imports
// Expected Version: 11
// Required Features: COLLECTIONS_FRAMEWORK, DIAMOND_OPERATOR, GENERICS, HTTP_CLIENT, METHOD_REFERENCES, OPTIONAL, STREAM_API
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.stream.*;
import java.util.*;

class MixedImports_Java11 {

    public void testMixedImports() throws Exception {
        // Explicit import
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new java.net.URI("https://example.com"))
            .build();

        // Wildcard import of java.util.stream
        Stream<String> stream = Stream.of("a", "b", "c");
        IntStream ints = IntStream.range(0, 10);

        // Wildcard import of java.util
        List<String> list = new ArrayList<>();
        Optional<String> opt = Optional.empty();

        // Java 11 String methods
        String text = "  hello world  ";
        boolean blank = text.isBlank();
        String stripped = text.strip();
        String repeated = "abc".repeat(3);
        text.lines().forEach(System.out::println);
    }
}