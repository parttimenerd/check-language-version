### Summary


Adds try-with-resources for automatic resource management via [`AutoCloseable`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/AutoCloseable.html).

### Details

Try-with-resources ensures that resources are closed automatically at the end of a `try` block.

A resource must implement [`AutoCloseable`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/AutoCloseable.html) (or [`Closeable`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/Closeable.html)).

Resources declared in the `try (...) {}` header are closed in reverse order, even if an exception is thrown.

This reduces boilerplate compared to `try/finally` and helps avoid resource leaks.

### Example

```java
// Read a file with try-with-resources
import java.io.BufferedReader;
import java.io.FileReader;

class Example {
    String demo(String path) throws Exception {
        try (BufferedReader r = new BufferedReader(new FileReader(path))) {
            return r.readLine(); // resource is closed automatically
        }
    }
}
```

### Historical

Introduced in Java 7 as part of Project Coin (JSR 334).

### Links

- [JLS §14.20.3 try-with-resources (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.20.3)
- [AutoCloseable (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/AutoCloseable.html)
- [JSR 334: Small Enhancements to the Java Programming Language](https://jcp.org/en/jsr/detail?id=334)