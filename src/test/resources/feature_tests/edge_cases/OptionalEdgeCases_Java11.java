// Edge case: Optional API evolution across Java versions (uses Java 16 features)
// Expected Version: 16
// Required Features: AUTOBOXING, COLLECTIONS_FRAMEWORK, COLLECTION_FACTORY_METHODS, GENERICS, LAMBDAS, METHOD_REFERENCES, OPTIONAL, RECORDS, STREAM_API
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

class OptionalEdgeCases_Java11 {

    // Java 8: Basic Optional creation
    public void testJava8Creation() {
        Optional<String> opt = Optional.of("hello");
        Optional<String> empty = Optional.empty();
        Optional<String> nullable = Optional.ofNullable(null);
    }

    // Java 8: Basic Optional operations
    public void testJava8Operations() {
        Optional<String> opt = Optional.of("hello");

        // Checking presence
        boolean present = opt.isPresent();

        // Getting value
        String value = opt.get();
        String orElse = opt.orElse("default");
        String orElseGet = opt.orElseGet(() -> "computed");
        String orElseThrowOld = opt.orElseThrow(() -> new RuntimeException());

        // Conditional action
        opt.ifPresent(System.out::println);
        opt.ifPresent(s -> System.out.println(s));
    }

    // Java 8: Optional transformations
    public void testJava8Transformations() {
        Optional<String> opt = Optional.of("hello");

        // map
        Optional<Integer> length = opt.map(String::length);
        Optional<String> upper = opt.map(s -> s.toUpperCase());

        // flatMap
        Optional<String> flat = opt.flatMap(s -> Optional.of(s.toUpperCase()));

        // filter
        Optional<String> filtered = opt.filter(s -> s.length() > 3);
        Optional<String> filtered2 = opt.filter(s -> !s.isEmpty());
    }

    // Java 9: Optional.or() - lazy alternative
    public void testJava9Or() {
        Optional<String> opt = Optional.empty();

        // or() returns alternative Optional lazily
        Optional<String> result = opt.or(() -> Optional.of("fallback"));
        Optional<String> chained = opt
            .or(() -> Optional.empty())
            .or(() -> Optional.of("final fallback"));
    }

    // Java 9: Optional.ifPresentOrElse()
    public void testJava9IfPresentOrElse() {
        Optional<String> opt = Optional.of("hello");
        Optional<String> empty = Optional.empty();

        // Handle both present and absent cases
        opt.ifPresentOrElse(
            value -> System.out.println("Found: " + value),
            () -> System.out.println("Not found")
        );

        empty.ifPresentOrElse(
            System.out::println,
            () -> System.out.println("Empty")
        );
    }

    // Java 9: Optional.stream()
    public void testJava9Stream() {
        Optional<String> opt = Optional.of("hello");
        Optional<String> empty = Optional.empty();

        // Convert Optional to Stream (0 or 1 element)
        Stream<String> stream = opt.stream();
        Stream<String> emptyStream = empty.stream();

        // Useful in flatMap
        List<Optional<String>> optionals = List.of(
            Optional.of("a"),
            Optional.empty(),
            Optional.of("b"),
            Optional.empty(),
            Optional.of("c")
        );

        // Flatten optionals to get only present values
        List<String> values = optionals.stream()
            .flatMap(Optional::stream)
            .toList();
    }

    // Java 10: Optional.orElseThrow() without argument
    public void testJava10OrElseThrow() {
        Optional<String> opt = Optional.of("hello");

        // No-arg orElseThrow() - throws NoSuchElementException if empty
        String value = opt.orElseThrow();

        // Compared to Java 8 version with supplier
        String value2 = opt.orElseThrow(() -> new IllegalStateException());
    }

    // Java 11: Optional.isEmpty() - direct calls for detection
    public void testJava11IsEmpty() {
        // Direct call on Optional type - detectable
        boolean directEmpty = Optional.empty().isEmpty();
        boolean directPresent = Optional.of("test").isEmpty();

        // Variable-based call
        Optional<String> opt = Optional.empty();
        boolean empty = opt.isEmpty();

        // In conditional
        if (Optional.ofNullable(null).isEmpty()) {
            System.out.println("Optional is empty");
        }

        // Negation pattern
        if (!Optional.of("value").isEmpty()) {
            System.out.println("Has value");
        }
    }

    // Java 11: Primitive Optionals with isEmpty
    public void testPrimitiveOptionalsIsEmpty() {
        OptionalInt optInt = OptionalInt.of(42);
        OptionalLong optLong = OptionalLong.of(100L);
        OptionalDouble optDouble = OptionalDouble.of(3.14);

        // Direct calls for detection
        boolean intEmpty = OptionalInt.empty().isEmpty();
        boolean longEmpty = OptionalLong.empty().isEmpty();
        boolean doubleEmpty = OptionalDouble.empty().isEmpty();

        // Variable-based
        boolean intCheck = optInt.isEmpty();
        boolean longCheck = optLong.isEmpty();
        boolean doubleCheck = optDouble.isEmpty();
    }

    // Primitive Optional operations
    public void testPrimitiveOptionalOperations() {
        // OptionalInt
        OptionalInt optInt = OptionalInt.of(42);
        int intValue = optInt.getAsInt();
        int intOrElse = optInt.orElse(0);
        optInt.ifPresent(System.out::println);

        // OptionalLong
        OptionalLong optLong = OptionalLong.of(100L);
        long longValue = optLong.getAsLong();
        long longOrElse = optLong.orElse(0L);

        // OptionalDouble
        OptionalDouble optDouble = OptionalDouble.of(3.14);
        double doubleValue = optDouble.getAsDouble();
        double doubleOrElse = optDouble.orElse(0.0);

        // From streams
        OptionalInt max = IntStream.of(1, 2, 3).max();
        OptionalDouble avg = IntStream.of(1, 2, 3).average();
        OptionalLong sum = LongStream.of(1, 2, 3).reduce(Long::sum);
    }

    // Combined Optional patterns
    public Optional<String> processValue(String input) {
        return Optional.ofNullable(input)
            .filter(s -> !s.isEmpty())
            .map(String::trim)
            .filter(s -> s.length() > 2)
            .or(() -> Optional.of("default"));
    }

    // Optional in method signatures
    public Optional<User> findUser(String id) {
        return Optional.empty();
    }

    public String getUserName(String id) {
        return findUser(id)
            .map(User::name)
            .orElse("Unknown");
    }

    // Helper record for testing
    record User(String id, String name, Optional<String> email) {}

    // Optional with records
    public void testOptionalWithRecords() {
        User user = new User("1", "John", Optional.of("john@example.com"));

        // Access optional field
        String email = user.email().orElse("no email");

        user.email().ifPresent(e -> System.out.println("Email: " + e));

        if (user.email().isEmpty()) {
            System.out.println("No email provided");
        }
    }
}