// Tiny: Static import looks like module (Java 5)
// Expected Version: 5
// Required Features: AUTOBOXING, STATIC_IMPORT

import static java.lang.System.out;
import static java.util.Arrays.asList;

public class Tiny_StaticLooksModule_Java5 {
    void test() { out.println(asList(1, 2, 3)); }
}