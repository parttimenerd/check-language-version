### Summary


Adds the built-in lightweight HTTP server via [`com.sun.net.httpserver.HttpServer`](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpServer.html).

### Details

The JDK includes a small, embeddable HTTP server implementation in the [`jdk.httpserver`](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.httpserver/module-summary.html) module.

The main entry point is [`HttpServer`](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpServer.html), which can:

- bind to an address via [`HttpServer.create(InetSocketAddress, int)`](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpServer.html#create(java.net.InetSocketAddress,int))
- register contexts via [`HttpServer.createContext(String, HttpHandler)`](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpServer.html#createContext(java.lang.String,com.sun.net.httpserver.HttpHandler))
- start serving via [`HttpServer.start()`](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpServer.html#start())

It is useful for testing, local tooling, and small services; production systems often use dedicated server frameworks.

### Example

```java
// Start a tiny HTTP server that responds with "ok"
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;

class Example {
    HttpServer demo() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/health", exchange -> {
            byte[] body = "ok".getBytes();
            exchange.sendResponseHeaders(200, body.length); // status + length
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        server.start();
        return server;
    }
}
```

### Historical

Introduced in Java 6 as a lightweight built-in HTTP server API.

### Links

- [HttpServer (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpServer.html)
- [Module jdk.httpserver (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.httpserver/module-summary.html)
- [HttpHandler (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpHandler.html)