### Summary


Adds the console I/O API via [`java.io.Console`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/Console.html).

### Details

[`Console`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/Console.html) provides text-based interaction with a user through a character terminal.

It is typically obtained via [`System.console()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/System.html#console()). If the JVM is not attached to an interactive console (for example, when running in an IDE or with redirected input/output), `System.console()` may return `null`.

Common operations include:

- Reading a line: [`Console.readLine()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/Console.html#readLine())
- Reading a password without echo: [`Console.readPassword()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/Console.html#readPassword())
- Writing formatted output: [`Console.format(String, Object...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/Console.html#format(java.lang.String,java.lang.Object...))

### Example

```java
// Read a username from the console (if available)
import java.io.Console;

class Example {
    void demo() {
        Console c = System.console(); // may be null in IDEs or when redirected
        if (c == null) return;
        String user = c.readLine("User: ");
        c.format("Hello %s%n", user); // write formatted output
    }
}
```

### Historical

Introduced in Java 6 to standardize interactive console access (especially for password input).

### Links

- [java.io.Console (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/Console.html)
- [System.console() (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/System.html#console())