// Java 21 edge case: Sequenced collections getFirst and getLast
// Test: Testing sequenced collection methods getFirst() and getLast()
// Expected Version: 21
// Required Features: COLLECTION_FACTORY_METHODS, GENERICS, SEQUENCED_COLLECTIONS
import java.util.*;
class Edge_SequencedGetFirstLast {
    void test() {
        // Use explicit SequencedCollection type for detection
        SequencedCollection<String> seq = List.of("a", "b", "c");
        String first = seq.getFirst();
        String last = seq.getLast();
    }
}