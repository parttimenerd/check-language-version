### Summary

Adds collection factory method [`List.copyOf(Collection)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html#copyOf(java.util.Collection)) (and related `Set.copyOf`/`Map.copyOf`) for creating unmodifiable copies.

### Details

Java 10 introduced `copyOf` factory methods on the collection interfaces:

- [`List.copyOf(Collection)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html#copyOf(java.util.Collection))
- [`Set.copyOf(Collection)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Set.html#copyOf(java.util.Collection))
- [`Map.copyOf(Map)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html#copyOf(java.util.Map))

These methods return **unmodifiable** collections. They are useful when you want to defensively copy mutable input data and then expose it safely.

Behavior highlights:

- The returned collection rejects mutation operations like [`List.add(E)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html#add(E)) by throwing [`UnsupportedOperationException`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/UnsupportedOperationException.html).
- `copyOf` may return the same instance if the input is already an unmodifiable collection produced by the JDK (an implementation detail), but callers must not rely on identity.
- Unlike wrapping with [`Collections.unmodifiableList(List)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collections.html#unmodifiableList(java.util.List)), `copyOf` performs a copy (so later mutations of the original collection won’t affect the returned one).

### Example

```java
// Create an unmodifiable defensive copy of a list
import java.util.ArrayList;
import java.util.List;

class Example {
    List<String> freeze(List<String> in) {
        List<String> out = List.copyOf(in); // Java 10: unmodifiable copy
        // out.add("x");                   // would throw UnsupportedOperationException
        return out;
    }

    void demo() {
        var src = new ArrayList<String>();
        src.add("a");
        var frozen = freeze(src);
        src.add("b"); // does not change 'frozen'
    }
}
```

### Historical

Introduced in Java 10 as part of the standard library; it complements the Java 9 collection factory methods like [`List.of(...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html#of()).

### Links

- [List.copyOf(Collection) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html#copyOf(java.util.Collection))
- [Set.copyOf(Collection) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Set.html#copyOf(java.util.Collection))
- [Map.copyOf(Map) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html#copyOf(java.util.Map))
- [Collections.unmodifiableList(List) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collections.html#unmodifiableList(java.util.List))