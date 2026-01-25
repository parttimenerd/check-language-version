// Java 9 edge case: Collection factory methods for all types
// Test: Testing List.of, Set.of, and Map.of factory methods
// Expected Version: 9
// Required Features: COLLECTION_FACTORY_METHODS
import java.util.*;

public class Edge_CollectionFactoryAllTypes_Java9 {
    void test() {
        List<String> list = List.of("a", "b", "c");
        Set<Integer> set = Set.of(1, 2, 3);
        Map<String, Integer> map = Map.of("one", 1, "two", 2);
        Map<String, String> bigMap = Map.ofEntries(
            Map.entry("k1", "v1"),
            Map.entry("k2", "v2")
        );
    }
}