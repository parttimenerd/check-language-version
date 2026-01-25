// Edge: Autoboxing in collections (Java 5)
// Expected Version: 5
// Required Features: AUTOBOXING, COLLECTIONS_FRAMEWORK, GENERICS
import java.util.*;
public class Edge_AutoboxingCollections {
    public void test() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(42);  // autoboxing
        int value = list.get(0);  // unboxing
    }
}