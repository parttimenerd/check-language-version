### Summary


Adds the process API via [`ProcessHandle`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ProcessHandle.html) for inspecting and controlling OS processes.

### Details

Java 9 introduced [`ProcessHandle`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ProcessHandle.html), which provides access to the native process ID and process metadata.

Common tasks include:

- getting the current process via [`ProcessHandle.current()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ProcessHandle.html#current())
- getting information via [`ProcessHandle.info()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ProcessHandle.html#info()) which returns [`ProcessHandle.Info`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ProcessHandle.Info.html)
- enumerating descendants via [`ProcessHandle.descendants()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ProcessHandle.html#descendants())

This complements the older [`Process`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Process.html) API, which focuses on processes started by the current JVM.

### Example

```java
// Print the current process ID
class Example {
    long demo() {
        return ProcessHandle.current().pid(); // native PID
    }
}
```

### Historical

Introduced in Java 9 to modernize Java's process support and expose process identifiers and metadata.

### Links

- [ProcessHandle (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ProcessHandle.html)
- [ProcessHandle.Info (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ProcessHandle.Info.html)
- [ProcessBuilder (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ProcessBuilder.html)