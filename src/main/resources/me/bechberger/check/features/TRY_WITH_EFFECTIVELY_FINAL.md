### Summary


Allows try-with-resources to manage *effectively final* resources declared before the `try` statement.

### Details

In Java 7 and 8, resources had to be declared directly in the `try (...)` header.

Java 9 allows an existing local variable to be used as a resource in try-with-resources, as long as it is **final** or *effectively final* (assigned only once).

This reduces boilerplate by avoiding a redundant re-declaration in the `try` header.

### Example

```java
// Use an effectively-final variable as a try-with-resources resource
import java.io.BufferedReader;
import java.io.FileReader;

class Example {
    String demo(String path) throws Exception {
        BufferedReader r = new BufferedReader(new FileReader(path));
        try (r) {
            return r.readLine(); // r will be closed automatically
        }
    }
}
```

### Historical

Introduced in Java 9 as a small language enhancement building on Java 7's try-with-resources.

### Links

- [JLS §14.20.3 try-with-resources (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.20.3)
- [Java 9 language changes overview (Oracle)](https://docs.oracle.com/javase/9/language/toc.htm)