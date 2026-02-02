### Summary


Adds virtual threads for lightweight concurrency via [`Thread.ofVirtual()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html#ofVirtual()).

### Details

Virtual threads are lightweight threads managed by the Java runtime rather than being mapped 1:1 to OS threads.

They are intended to make it practical to use a thread-per-task style for high-concurrency applications, especially for blocking I/O.

Virtual threads are created via builders like:

- [`Thread.ofVirtual()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html#ofVirtual())

Many existing blocking APIs can be used unchanged; the runtime can park/unpark virtual threads without blocking an OS thread.

### Example

```java
// Start a virtual thread
class Example {
    Thread demo() {
        return Thread.ofVirtual().start(() -> {
            System.out.println("running"); // work in a virtual thread
        });
    }
}
```

### Historical

Standardized in Java 21 via JEP 444 (Project Loom) after preview in earlier releases.

### Links

- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [Thread.ofVirtual() (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html#ofVirtual())
- [Thread (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html)