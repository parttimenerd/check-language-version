### Summary


Adds file system watch support via [`WatchService`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/WatchService.html).

### Details

The watch service API allows applications to receive notifications when directory entries change.

Core types:

- [`WatchService`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/WatchService.html) receives events
- [`WatchKey`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/WatchKey.html) represents a registration and batches events
- [`WatchEvent`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/WatchEvent.html) represents a single event

A directory is registered with a watch service via [`Path.register(WatchService, WatchEvent.Kind...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Path.html#register(java.nio.file.WatchService,java.nio.file.WatchEvent.Kind...)).

Consumers typically:

- block on [`WatchService.take()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/WatchService.html#take())
- process events from the key
- call [`WatchKey.reset()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/WatchKey.html#reset()) to continue receiving

### Example

```java
// Watch a directory for create events (illustrative)
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;

class Example {
    void demo(Path dir) throws Exception {
        try (var ws = FileSystems.getDefault().newWatchService()) {
            dir.register(ws, StandardWatchEventKinds.ENTRY_CREATE); // register
            WatchKey key = ws.take(); // wait for events
            key.pollEvents(); // read events
            key.reset(); // continue watching
        }
    }
}
```

### Historical

Introduced in Java 7 as part of NIO.2 (JSR 203).

### Links

- [WatchService (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/WatchService.html)
- [Path.register(...) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Path.html#register(java.nio.file.WatchService,java.nio.file.WatchEvent.Kind...))
- [JSR 203: More New I/O APIs for the Java Platform](https://jcp.org/en/jsr/detail?id=203)