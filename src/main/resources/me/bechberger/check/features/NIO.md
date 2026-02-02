### Summary

Adds the New I/O (NIO) APIs in [`java.nio`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/package-summary.html) (buffers, channels, and related utilities).

### Details

NIO introduced buffer-based I/O primitives and a channel-based I/O model.

Key concepts and types include:

- Buffers like [`ByteBuffer`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/ByteBuffer.html) for storing and manipulating typed data with position/limit/mark
- Channels like [`ReadableByteChannel`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/channels/ReadableByteChannel.html) and [`FileChannel`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/channels/FileChannel.html) for I/O operations
- Character encoding via [`Charset`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/charset/Charset.html) and related classes in [`java.nio.charset`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/charset/package-summary.html)

Compared to classic stream-based I/O (`java.io`), NIO provides lower-level primitives that are useful for high-performance I/O, non-blocking designs, and memory-mapped file access.

### Example

```java
// Read a file into a ByteBuffer using NIO
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

class Example {
    byte[] readAll(Path p) throws IOException {
        try (FileChannel ch = FileChannel.open(p, StandardOpenOption.READ)) {
            ByteBuffer buf = ByteBuffer.allocate((int) ch.size()); // allocate buffer
            ch.read(buf); // fill buffer
            return buf.array();
        }
    }
}
```

### Historical

Introduced in Java 1.4 as the first NIO APIs. Java 7 later expanded this area significantly with NIO.2 (`java.nio.file`).

### Links

- [Package java.nio (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/package-summary.html)
- [Package java.nio.channels (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/channels/package-summary.html)
- [ByteBuffer (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/ByteBuffer.html)
- [FileChannel (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/channels/FileChannel.html)