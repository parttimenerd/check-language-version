// Java 9 combination: Anonymous diamond + Collection factory methods
// Test: Combination of diamond operator with anonymous classes and collection factory methods
// Expected Version: 9
// Required Features: DIAMOND_WITH_ANONYMOUS, COLLECTION_FACTORY_METHODS
import java.util.*;
public class Combo_AnonymousDiamondFactory_Java9 {
    void test() {
        List<String> items = List.of("a", "b", "c");
        List<String> custom = new ArrayList<>() {
            { addAll(items); }
        };
    }
}