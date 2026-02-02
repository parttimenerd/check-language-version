### Summary


Adds multi-catch to catch multiple exception types in one `catch` clause.

### Details

Multi-catch lets you handle several exception types with a single `catch` block by separating the types with `|`.

Example shape:

- `catch (IOException | SQLException e) { ... }`

The caught exception parameter is implicitly `final` (you can’t reassign it) because it represents multiple possible types.

Multi-catch is often used when multiple exceptions should be handled the same way (logging, wrapping, or translating to a domain exception).

### Example

```java
// Catch multiple exception types with one handler
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class Example {
    String demo(Path p) {
        try {
            return Files.readString(p);
        } catch (IOException | SecurityException e) {
            return "error"; // shared handling
        }
    }
}
```

### Historical

Introduced in Java 7 as part of Project Coin (JSR 334).

### Links

- [JLS §11.2 Compile-Time Checking of Exceptions (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-11.html#jls-11.2)
- [JLS §14.20 The try statement (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.20)
- [JSR 334: Small Enhancements to the Java Programming Language](https://jcp.org/en/jsr/detail?id=334)