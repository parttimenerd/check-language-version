// Java 9 edge case: Anonymous class with diamond
// Test: Testing diamond operator with anonymous class instantiation
// Expected Version: 9
// Required Features: DIAMOND_WITH_ANONYMOUS
import java.util.*;
public class Edge_AnonymousDiamond_Java9 {
    List<String> list = new ArrayList<>() {
        { add("initial"); }
    };
}