// Edge case: Fully qualified names without any imports
// Expected Version: 11
// Required Features: AUTOBOXING, COLLECTIONS_FRAMEWORK, DATE_TIME_API, GENERICS, HTTP_CLIENT, OPTIONAL, STREAM_API
class FullyQualifiedNames_NoImports {

    public void testFullyQualifiedTypes() {
        // Java 8: Stream API - fully qualified
        java.util.stream.Stream<String> stream = java.util.stream.Stream.of("a", "b");

        // Java 8: Date/Time API - fully qualified
        java.time.LocalDate date = java.time.LocalDate.now();
        java.time.LocalDateTime dateTime = java.time.LocalDateTime.now();

        // Java 8: Optional - fully qualified
        java.util.Optional<String> opt = java.util.Optional.of("hello");

        // Java 11: HTTP Client - fully qualified
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
    }

    public void testFullyQualifiedMethodCalls() {
        // Java 9: Collection factory methods - fully qualified
        java.util.List<String> list = java.util.List.of("a", "b", "c");
        java.util.Set<Integer> set = java.util.Set.of(1, 2, 3);
        java.util.Map<String, Integer> map = java.util.Map.of("a", 1, "b", 2);

        // Java 10: Collection.copyOf - fully qualified
        java.util.List<String> copy = java.util.List.copyOf(list);
    }

    public void testFullyQualifiedFieldAccess() {
        // Fully qualified field access
        java.lang.System.out.println("Hello");
    }
}