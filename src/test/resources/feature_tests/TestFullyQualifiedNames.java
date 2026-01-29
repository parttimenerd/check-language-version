// Test file for fully qualified name detection
// Expected Version: 21
// Required Features: AUTOBOXING, AWT, BASE64_API, COLLECTIONS_FRAMEWORK, CONCURRENT_API, DATE_TIME_API, DIAMOND_OPERATOR, GENERICS, HEX_FORMAT, HTTP_CLIENT, LAMBDAS, NIO, NIO2, OPTIONAL, REGEX, STREAM_API, SWING, VIRTUAL_THREADS
// This file uses fully qualified class names without imports

class TestFullyQualifiedNames {

    public void testJava8Features() {
        // Stream API - fully qualified
        java.util.stream.Stream<String> stream = java.util.stream.Stream.of("a", "b", "c");

        // Date/Time API - fully qualified
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // Optional - fully qualified
        java.util.Optional<String> opt = java.util.Optional.of("hello");

        // Base64 - fully qualified
        java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
    }

    public void testJava9Features() {
        // Collection factory methods - fully qualified
        java.util.List<String> list = java.util.List.of("a", "b");
        java.util.Set<Integer> set = java.util.Set.of(1, 2, 3);
        java.util.Map<String, Integer> map = java.util.Map.of("a", 1);
    }

    public void testJava11Features() {
        // HTTP Client - fully qualified
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
    }

    public void testJava17Features() {
        // HexFormat - fully qualified
        java.util.HexFormat hex = java.util.HexFormat.of();
    }

    public void testJava21Features() {
        // Virtual Threads - fully qualified method call
        Thread vt = Thread.ofVirtual().start(() -> {});
    }

    public void testLegacyFeatures() {
        // AWT - fully qualified
        java.awt.Color color = new java.awt.Color(255, 0, 0);

        // Swing - fully qualified
        javax.swing.JFrame frame = new javax.swing.JFrame("Test");

        // Collections - fully qualified
        java.util.ArrayList<String> arrayList = new java.util.ArrayList<>();

        // Regex - fully qualified
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(".*");

        // NIO - fully qualified
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(1024);

        // NIO.2 - fully qualified
        java.nio.file.Path path = java.nio.file.Path.of("/tmp/test");

        // Concurrent - fully qualified
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(4);
    }
}