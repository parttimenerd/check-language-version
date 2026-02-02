### Summary


Adds the enhanced `for` loop ("for-each") for iterating over arrays and `Iterable`.

### Details

The enhanced `for` statement provides a concise syntax to iterate over:

- arrays
- objects that implement [`Iterable`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Iterable.html)

For `Iterable`, the loop is defined in terms of acquiring an [`Iterator`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Iterator.html) via [`Iterable.iterator()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Iterable.html#iterator()) and repeatedly calling [`Iterator.hasNext()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Iterator.html#hasNext()) / [`Iterator.next()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Iterator.html#next()).

This reduces boilerplate compared to manual index/iterator loops and is commonly used for read-only iteration.

### Example

```java
// Iterate over a list using the enhanced for loop
import java.util.List;

class Example {
    int sum(List<Integer> xs) {
        int s = 0;
        for (int x : xs) { // x takes each element from the Iterable
            s += x;
        }
        return s;
    }
}
```

### Historical

Introduced in Java 5 (JLS update; commonly associated with JSR 201 language changes) alongside generics and other language improvements.

### Links

- [JLS 14.14.2 The enhanced for statement (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.14.2)
- [Iterable (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Iterable.html)
- [Iterator (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Iterator.html)