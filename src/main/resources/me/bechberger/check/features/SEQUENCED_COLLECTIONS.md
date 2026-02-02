### Summary


Adds sequenced collection interfaces (`SequencedCollection`, `SequencedSet`, `SequencedMap`) for uniform first/last operations.

### Details

Java 21 introduced new interfaces in `java.util` to provide a consistent way to work with collections that have a well-defined encounter order.

Key types include:

- [`SequencedCollection`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/SequencedCollection.html)
- [`SequencedSet`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/SequencedSet.html)
- [`SequencedMap`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/SequencedMap.html)

These interfaces add operations such as:

- `getFirst()` / `getLast()`
- `removeFirst()` / `removeLast()`
- `reversed()` to view the collection in reverse order

Several existing JDK types (for example, `List`, `Deque`, `LinkedHashMap`) were updated to implement these interfaces.

### Example

```java
// Use SequencedCollection operations (Java 21+)
import java.util.ArrayList;

class Example {
    int demo() {
        var xs = new ArrayList<Integer>();
        xs.add(1);
        xs.add(2);
        return xs.getFirst(); // first element
    }
}
```

### Historical

Introduced in Java 21 via JEP 431 to unify ordered collection APIs.

### Links

- [JEP 431: Sequenced Collections](https://openjdk.org/jeps/431)
- [SequencedCollection (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/SequencedCollection.html)
- [SequencedMap (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/SequencedMap.html)