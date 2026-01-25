// Java 8 edge case: Stream chain with all operations
// Test: Testing stream pipeline with filter, map, flatMap, reduce, collect
// Expected Version: 8
// Required Features: COLLECTIONS_FRAMEWORK, GENERICS, LAMBDAS, METHOD_REFERENCES, STREAM_API
import java.util.*;
import java.util.stream.*;

public class Edge_StreamChainAllOperations {
    void test() {
        List<String> result = Stream.of("a", "b", "c")
            .filter(s -> s.length() > 0)
            .map(String::toUpperCase)
            .flatMap(s -> Stream.of(s, s))
            .distinct()
            .sorted()
            .limit(10)
            .collect(Collectors.toList());
    }
}