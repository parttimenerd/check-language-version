### Summary


Adds the core I/O APIs in [`java.io`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/package-summary.html).

### Details

The [`java.io`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/package-summary.html) package provides classic stream-based input and output.

Key abstractions include:

- Byte streams: [`InputStream`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/InputStream.html) and [`OutputStream`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/OutputStream.html)
- Character streams: [`Reader`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/Reader.html) and [`Writer`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/Writer.html)
- Buffering: [`BufferedInputStream`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/BufferedInputStream.html) and [`BufferedReader`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/BufferedReader.html)
- File abstraction via [`File`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/File.html)

Modern Java often prefers NIO.2 (`java.nio.file`) for file operations, but `java.io` remains widely used and is foundational across the JDK.

### Example

```java
// Read a line of text from standard input
import java.io.BufferedReader;
import java.io.InputStreamReader;

class Example {
    String demo() throws Exception {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in)); // wrap stdin
        return r.readLine(); // read one line
    }
}
```

### Historical

Introduced in Java 1.0 as part of the original Java standard library.

### Links

- [Package java.io (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/package-summary.html)
- [InputStream (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/InputStream.html)
- [Reader (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/Reader.html)
- [File (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/File.html)