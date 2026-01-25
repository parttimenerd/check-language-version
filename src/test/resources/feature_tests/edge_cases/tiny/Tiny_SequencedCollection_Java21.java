// Test: SequencedCollection (Java 21)
// Expected Version: 21
// Required Features: COLLECTIONS_FRAMEWORK, DIAMOND_OPERATOR, GENERICS, SEQUENCED_COLLECTIONS
import java.util.*;
public class Tiny_SequencedCollection_Java21 {
    void test() {
        // Use explicit SequencedCollection type for detection
        SequencedCollection<String> seq = new ArrayList<>();
        String first = seq.getFirst();
        String last = seq.getLast();
        seq.addFirst("a");
        seq.addLast("z");
    }
}