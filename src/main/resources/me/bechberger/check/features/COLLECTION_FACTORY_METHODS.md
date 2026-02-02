### Summary

Adds collection factory methods like [`List.of(...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html#of()), [`Set.of(...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Set.html#of()), and [`Map.of(...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html#of()).

### Details

Java 9 added convenient factory methods to create small, **unmodifiable** collections.

Common entry points are:

- [`List.of(...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html#of())
- [`Set.of(...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Set.html#of())
- [`Map.of(...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html#of())
- [`Map.ofEntries(Map.Entry...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html#ofEntries(java.util.Map.Entry...)) together with [`Map.entry(K, V)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html#entry(K,V))

Key properties:

- The returned collections are unmodifiable; calling mutating methods like [`List.add(E)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html#add(E)) throws [`UnsupportedOperationException`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/UnsupportedOperationException.html).
- `List.of(...)` and `Set.of(...)` reject `null` elements and throw [`NullPointerException`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/NullPointerException.html) if passed `null`.
- `Set.of(...)` and `Map.of(...)` reject duplicates and throw [`IllegalArgumentException`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/IllegalArgumentException.html) for duplicate elements/keys.

These factory methods are especially useful for constants, tests, and APIs that need to return small read-only collections without the verbosity of using mutable implementations and then wrapping them.

### Example

```java
// Java 9 collection factories create concise unmodifiable collections
import java.util.List;
import java.util.Map;
import java.util.Set;

class Example {
    void demo() {
        List<String> xs = List.of("a", "b");          // unmodifiable list
        Set<Integer> ys = Set.of(1, 2, 3);             // unmodifiable set
        Map<String, Integer> m = Map.of("a", 1, "b", 2); // unmodifiable map

        // xs.add("c"); // would throw UnsupportedOperationException
    }
}
```

### Historical

Introduced in Java 9 as part of the standard library to provide concise creation of unmodifiable collections. Java 10 later added related copy factories like [`List.copyOf(Collection)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html#copyOf(java.util.Collection)).

### Links

- [List.of(...) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html#of())
- [Set.of(...) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Set.html#of())
- [Map.of(...) and Map.ofEntries(...) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html)
- [UnsupportedOperationException (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/UnsupportedOperationException.html)