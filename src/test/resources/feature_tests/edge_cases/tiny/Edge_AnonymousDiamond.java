// Java 9 edge case: Anonymous class with diamond
// Test: Testing diamond operator with anonymous class instantiation
// Expected Version: 9
// Required Features: COLLECTIONS_FRAMEWORK, DIAMOND_OPERATOR, DIAMOND_WITH_ANONYMOUS, GENERICS, INNER_CLASSES
import java.util.*;
class Edge_AnonymousDiamond {
    List<String> list = new ArrayList<>() {
        { add("initial"); }
    };
}