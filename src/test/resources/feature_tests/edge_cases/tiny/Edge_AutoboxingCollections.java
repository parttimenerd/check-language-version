// Edge: Autoboxing in collections (Java 5)
// Expected Version: 5
// Required Features: AUTOBOXING, GENERICS
import java.util.*;
public class Edge_AutoboxingCollections_Java5 {
    public void test() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(42);  // autoboxing
        int value = list.get(0);  // unboxing
    }
}