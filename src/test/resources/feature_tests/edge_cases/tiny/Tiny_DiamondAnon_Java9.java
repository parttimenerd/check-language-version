// Test: Diamond with anonymous class (Java 9)
// Expected Version: 9
// Required Features: DIAMOND_WITH_ANONYMOUS
import java.util.*;
public class Tiny_DiamondAnon_Java9 {
    Comparator<String> c = new Comparator<>() {
        public int compare(String a, String b) { return a.length() - b.length(); }
    };
}