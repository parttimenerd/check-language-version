// Tiny: Collection.copyOf (Java 10)
// Expected Version: 10
// Required Features: COLLECTION_COPY_OF

import java.util.*;

public class Tiny_CopyOf_Java10 {
    void test() {
        var copy = List.copyOf(List.of("a", "b"));
    }
}