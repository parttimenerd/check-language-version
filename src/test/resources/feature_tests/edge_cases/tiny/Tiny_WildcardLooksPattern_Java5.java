// Tiny: Generic wildcard looks like pattern (Java 5)
// Expected Version: 5
// Required Features: COLLECTIONS_FRAMEWORK, FOR_EACH, GENERICS

import java.util.*;

class Tiny_WildcardLooksPattern_Java5 {
    void process(List<? extends Number> nums) {
        for (Number n : nums) System.out.println(n);
    }
}