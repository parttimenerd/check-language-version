// Tiny: Sequenced collections (Java 21)
// Expected Version: 21
// Required Features: SEQUENCED_COLLECTIONS

import java.util.*;

public class Tiny_SeqColl_Java21 {
    void test() {
        SequencedCollection<String> sc = new ArrayList<>();
        sc.addFirst("a");
        sc.getLast();
    }
}