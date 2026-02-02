### Summary


Adds implicit import support for [`java.io.IO`](hhttps://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/IO.html) in compact source files.

### Details

In the simplified/compact source file mode introduced for small programs, certain commonly used types can be made available without explicit import statements.

This feature covers implicitly importing [`java.lang.IO`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/IO.html), a utility type intended to simplify common I/O use cases in these small programs.

### Example

```java
// Compact source file style: IO is available without an explicit import
void main() {
    IO.println("Hello"); // example usage of java.io.IO
}
```

### Historical

Introduced in Java 25 alongside compact source files and instance main methods.

### Links

- [java.lang.IO (Oracle Javadoc, Java 25)](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/IO.html)
- [JEP 512: Compact Source Files and Instance Main Methods](https://openjdk.org/jeps/512)