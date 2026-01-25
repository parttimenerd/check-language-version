// Tiny: Diamond anonymous (Java 9)
// Expected Version: 9
// Required Features: DIAMOND_OPERATOR, DIAMOND_WITH_ANONYMOUS, GENERICS, INNER_CLASSES

import java.util.*;

public class Tiny_DiamondAnon_Java9 {
    Comparator<String> c = new Comparator<>() {
        public int compare(String a, String b) { return 0; }
    };
}