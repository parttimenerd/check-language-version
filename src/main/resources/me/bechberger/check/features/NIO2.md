### Summary

Adds NIO.2 file system APIs in [`java.nio.file`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/package-summary.html) (e.g., `Path`/`Files`).

### Details

Java 7 introduced NIO.2, a major expansion of NIO focused on file systems.

Core types include:

- [`Path`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Path.html) for representing file system paths
- [`Files`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Files.html) as a utility class for common operations (read/write/copy/move)
- Directory iteration via [`DirectoryStream`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/DirectoryStream.html)
- File watching via [`WatchService`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/WatchService.html)

The APIs are designed to work across different file system providers and support features like symbolic links and file attribute views.

### Example

```java
// Robustly read a UTF-8 text file using java.nio.file
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

class Example {
    String demo(Path p) throws Exception {
        return Files.readString(p, StandardCharsets.UTF_8); // read whole file
    }
}
```

### Historical

Introduced in Java 7 as NIO.2, adding `java.nio.file` and the service-provider based file system API.

### Links

- [Package java.nio.file (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/package-summary.html)
- [Path (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Path.html)
- [Files (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Files.html)
- [WatchService (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/WatchService.html)