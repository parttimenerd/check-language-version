// Java 8 feature: Stream API
// Expected Version: 8
// Required Features: AUTOBOXING, COLLECTIONS_FRAMEWORK, GENERICS, LAMBDAS, METHOD_REFERENCES, STREAM_API
import java.util.stream.*;
import java.util.*;

class Java8_StreamAPI {
    public void method() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        // Stream operations
        int sum = numbers.stream()
            .filter(n -> n % 2 == 0)
            .mapToInt(Integer::intValue)
            .sum();

        // Collectors
        List<String> strings = numbers.stream()
            .map(Object::toString)
            .collect(Collectors.toList());

        // Parallel stream
        long count = numbers.parallelStream()
            .filter(n -> n > 2)
            .count();
    }
}