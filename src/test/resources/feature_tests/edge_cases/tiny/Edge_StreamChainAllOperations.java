// Java 8 edge case: Stream chain with all operations
// Test: Testing stream pipeline with filter, map, flatMap, reduce, collect
// Expected Version: 8
// Required Features: STREAM_API, LAMBDAS
import java.util.*;
import java.util.stream.*;

public class Edge_StreamChainAllOperations_Java8 {
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