// Tiny: Generic wildcard looks like pattern (Java 5)
// Expected Version: 5
// Required Features: GENERICS

import java.util.*;

public class Tiny_WildcardLooksPattern_Java5 {
    void process(List<? extends Number> nums) {
        for (Number n : nums) System.out.println(n);
    }
}