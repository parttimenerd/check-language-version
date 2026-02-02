### Summary


Adds the built-in logging framework in [`java.util.logging`](https://docs.oracle.com/en/java/javase/21/docs/api/java.logging/java/util/logging/package-summary.html).

### Details

The `java.util.logging` (JUL) APIs provide a standard logging framework in the JDK.

Key concepts include:

- [`Logger`](https://docs.oracle.com/en/java/javase/21/docs/api/java.logging/java/util/logging/Logger.html) as the main logging entry point
- log levels via [`Level`](https://docs.oracle.com/en/java/javase/21/docs/api/java.logging/java/util/logging/Level.html) (e.g., `INFO`, `WARNING`)
- handlers such as [`ConsoleHandler`](https://docs.oracle.com/en/java/javase/21/docs/api/java.logging/java/util/logging/ConsoleHandler.html)
- formatting via [`Formatter`](https://docs.oracle.com/en/java/javase/21/docs/api/java.logging/java/util/logging/Formatter.html)

It is commonly used for built-in JDK logging and small applications; many larger applications use other logging abstractions but can still bridge to JUL.

### Example

```java
// Log a message using java.util.logging
import java.util.logging.Level;
import java.util.logging.Logger;

class Example {
    private static final Logger LOG = Logger.getLogger(Example.class.getName());

    void demo() {
        LOG.log(Level.INFO, "Hello logging"); // log with level
    }
}
```

### Historical

Introduced in Java 1.4 as the first standardized logging framework shipped with the JDK.

### Links

- [Package java.util.logging (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.logging/java/util/logging/package-summary.html)
- [Logger (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.logging/java/util/logging/Logger.html)
- [Level (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.logging/java/util/logging/Level.html)