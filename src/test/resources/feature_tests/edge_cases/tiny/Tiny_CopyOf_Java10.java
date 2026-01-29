// Tiny: Collection.copyOf (Java 10)
// Expected Version: 10
// Required Features: COLLECTION_COPY_OF, COLLECTION_FACTORY_METHODS, VAR

import java.util.*;

class Tiny_CopyOf_Java10 {
    void test() {
        var copy = List.copyOf(List.of("a", "b"));
    }
}