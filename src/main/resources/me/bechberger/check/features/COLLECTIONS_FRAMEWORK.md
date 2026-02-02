### Summary

Adds the Java Collections Framework APIs (e.g., `List`, `Set`, `Map`) in the `java.util` package.

### Details

The Java Collections Framework provides standard interfaces and implementations for working with groups of objects.

Core interfaces include:

- [`java.util.List`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html) for ordered collections
- [`java.util.Set`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Set.html) for collections with no duplicates
- [`java.util.Map`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html) for key/value mappings

Common implementations include:

- [`java.util.ArrayList`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/ArrayList.html)
- [`java.util.HashSet`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HashSet.html)
- [`java.util.HashMap`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HashMap.html)

A key design aspect is programming to interfaces (e.g., `List`) rather than concrete classes (e.g., `ArrayList`). The framework also includes algorithms and helpers via [`java.util.Collections`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collections.html) (e.g., [`Collections.sort(List)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collections.html#sort(java.util.List))) and iteration support via [`java.util.Iterator`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Iterator.html).

### Example

```java
// Basic use of the Collections Framework
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Example {
    void demo() {
        List<String> xs = new ArrayList<>(); // program to the List interface
        xs.add("b");
        xs.add("a");
        Collections.sort(xs); // sort the list in-place
    }
}
```

### Historical

Introduced in Java 1.2 as a major standardization of collection APIs (replacing earlier ad-hoc types such as `Vector`/`Hashtable` as the primary recommended abstractions).

### Links

- [Package java.util (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/package-summary.html)
- [List (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html)
- [Map (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html)
- [Collections.sort(List) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collections.html#sort(java.util.List))