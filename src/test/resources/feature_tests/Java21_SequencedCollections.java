// Java 21 feature: Sequenced Collections (JEP 431)
// Expected Version: 21
// Required Features: SEQUENCED_COLLECTIONS
import java.util.SequencedCollection;
import java.util.SequencedSet;
import java.util.SequencedMap;
import java.util.*;

public class Java21_SequencedCollections {
    public void method() {
        // SequencedCollection methods
        LinkedList<String> list = new LinkedList<>();
        list.addFirst("first");
        list.addLast("last");
        String first = list.getFirst();
        String last = list.getLast();
        list.removeFirst();
        list.removeLast();

        // Reversed view
        SequencedCollection<String> reversed = list.reversed();
    }
}