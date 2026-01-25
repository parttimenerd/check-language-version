// Java 8 feature: Optional class
// Expected Version: 8
// Required Features: OPTIONAL
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.OptionalDouble;

public class Java8_Optional {
    public void testOptional() {
        // Create Optional instances
        Optional<String> name = Optional.of("John");
        Optional<String> empty = Optional.empty();
        Optional<String> nullable = Optional.ofNullable(null);

        // Check if value is present
        if (name.isPresent()) {
            System.out.println("Name: " + name.get());
        }

        // ifPresent with consumer
        name.ifPresent(System.out::println);

        // orElse and orElseGet
        String value = empty.orElse("Unknown");
        String lazyValue = empty.orElseGet(() -> "Computed");

        // orElseThrow
        try {
            empty.orElseThrow(() -> new RuntimeException("No value"));
        } catch (RuntimeException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // map and flatMap
        Optional<Integer> length = name.map(String::length);

        // filter
        Optional<String> longName = name.filter(n -> n.length() > 3);

        // Primitive optionals
        OptionalInt optInt = OptionalInt.of(42);
        OptionalLong optLong = OptionalLong.of(100L);
        OptionalDouble optDouble = OptionalDouble.of(3.14);

        int intValue = optInt.orElse(0);
        System.out.println("Int value: " + intValue);
    }
}