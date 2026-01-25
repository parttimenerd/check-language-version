// Java 21 edge case: Sequenced collections getFirst and getLast
// Test: Testing sequenced collection methods getFirst() and getLast()
// Expected Version: 21
// Required Features: SEQUENCED_COLLECTIONS
import java.util.*;
public class Edge_SequencedGetFirstLast_Java21 {
    void test() {
        // Use explicit SequencedCollection type for detection
        SequencedCollection<String> seq = List.of("a", "b", "c");
        String first = seq.getFirst();
        String last = seq.getLast();
    }
}