// Test: Collection factory methods (Java 9)
// Expected Version: 9
// Required Features: COLLECTION_FACTORY_METHODS
import java.util.*;
public class Tiny_CollectionFactory_Java9 {
    List<String> l = List.of("a", "b");
    Set<Integer> s = Set.of(1, 2, 3);
    Map<String, Integer> m = Map.of("a", 1);
}