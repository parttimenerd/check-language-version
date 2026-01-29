// Java 9 combination: Anonymous diamond + Collection factory methods
// Test: Combination of diamond operator with anonymous classes and collection factory methods
// Expected Version: 9
// Required Features: COLLECTIONS_FRAMEWORK, COLLECTION_FACTORY_METHODS, DIAMOND_OPERATOR, DIAMOND_WITH_ANONYMOUS, GENERICS, INNER_CLASSES
import java.util.*;
class Combo_AnonymousDiamondFactory_Java9 {
    void test() {
        List<String> items = List.of("a", "b", "c");
        List<String> custom = new ArrayList<>() {
            { addAll(items); }
        };
    }
}