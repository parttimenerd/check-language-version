### Summary


Adds support for Unix domain sockets via [`UnixDomainSocketAddress`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/UnixDomainSocketAddress.html).

### Details

Unix domain sockets provide local inter-process communication (IPC) using filesystem paths rather than IP addresses.

Java supports Unix domain sockets primarily through NIO channels:

- [`SocketChannel`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/channels/SocketChannel.html)
- [`ServerSocketChannel`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/channels/ServerSocketChannel.html)

Addresses are represented by [`UnixDomainSocketAddress`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/UnixDomainSocketAddress.html), typically created via [`UnixDomainSocketAddress.of(String)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/UnixDomainSocketAddress.html#of(java.lang.String)).

### Example

```java
// Create a Unix domain socket address (illustrative)
import java.net.UnixDomainSocketAddress;

class Example {
    UnixDomainSocketAddress demo() {
        return UnixDomainSocketAddress.of("/tmp/app.sock"); // filesystem path
    }
}
```

### Historical

Standardized in Java 16 via JEP 380 to add Unix-domain socket support to the platform.

### Links

- [JEP 380: Unix-Domain Socket Channels](https://openjdk.org/jeps/380)
- [UnixDomainSocketAddress (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/UnixDomainSocketAddress.html)
- [SocketChannel (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/channels/SocketChannel.html)