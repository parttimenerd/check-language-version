// Tiny: For-each looks like stream (Java 5)
// Expected Version: 5
// Required Features: COLLECTIONS_FRAMEWORK, FOR_EACH, GENERICS

import java.util.*;

public class Tiny_ForEachLooksStream_Java5 {
    void process(List<String> items) {
        for (String item : items) System.out.println(item);
    }
}