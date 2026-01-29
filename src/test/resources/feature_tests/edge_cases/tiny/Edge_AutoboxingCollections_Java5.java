// Java 5 edge case: Autoboxing in collections
// Test: Testing autoboxing and unboxing with generic collections
// Expected Version: 5
// Required Features: AUTOBOXING, COLLECTIONS_FRAMEWORK, GENERICS
import java.util.*;
class Edge_AutoboxingCollections_Java5 {
    void test() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(42); // autoboxing
        int x = list.get(0); // unboxing
    }
}