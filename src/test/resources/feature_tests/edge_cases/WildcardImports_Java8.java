// Edge case: Wildcard imports should detect all features from the package
// Expected Version: 8
// Required Features: COLLECTIONS_FRAMEWORK, DATE_TIME_API, DIAMOND_OPERATOR, GENERICS, OPTIONAL, STREAM_API
import java.util.stream.*;
import java.time.*;
import java.util.*;

class WildcardImports_Java8 {
    public void testWildcardImports() {
        // Stream API via wildcard import
        Stream<String> stream = Stream.of("a", "b", "c");

        // Date/Time API via wildcard import
        LocalDate date = LocalDate.now();
        LocalDateTime dateTime = LocalDateTime.now();
        ZonedDateTime zoned = ZonedDateTime.now();

        // Optional via wildcard import of java.util
        Optional<String> opt = Optional.of("hello");

        // Collections via wildcard import
        List<String> list = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
    }
}