### Summary

Adds the HTTP Client API in [`java.net.http`](https://docs.oracle.com/en/java/javase/21/docs/api/java.net.http/java/net/http/package-summary.html).

### Details

Java 11 standardized the HTTP Client API for making HTTP/1.1 and HTTP/2 requests.

Key types include:

- [`HttpClient`](https://docs.oracle.com/en/java/javase/21/docs/api/java.net.http/java/net/http/HttpClient.html) for configuring and sending requests
- [`HttpRequest`](https://docs.oracle.com/en/java/javase/21/docs/api/java.net.http/java/net/http/HttpRequest.html) for building an HTTP request
- [`HttpResponse`](https://docs.oracle.com/en/java/javase/21/docs/api/java.net.http/java/net/http/HttpResponse.html) for receiving responses

Requests are typically created via [`HttpRequest.newBuilder(URI)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.net.http/java/net/http/HttpRequest.html#newBuilder(java.net.URI)) and sent synchronously with [`HttpClient.send(HttpRequest, BodyHandler)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.net.http/java/net/http/HttpClient.html#send(java.net.http.HttpRequest,java.net.http.HttpResponse.BodyHandler)) or asynchronously with `sendAsync`.

### Example

```java
// Send a simple GET request using java.net.http
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class Example {
    String demo() throws Exception {
        HttpClient client = HttpClient.newHttpClient(); // default client
        HttpRequest req = HttpRequest.newBuilder(URI.create("https://example.com"))
                .GET() // explicit GET
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.body();
    }
}
```

### Historical

Introduced in Java 11 as a standard API, replacing the earlier incubating HTTP client from Java 9/10.

### Links

- [Package java.net.http (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.net.http/java/net/http/package-summary.html)
- [HttpClient (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.net.http/java/net/http/HttpClient.html)
- [HttpRequest (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.net.http/java/net/http/HttpRequest.html)
- [HttpResponse (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.net.http/java/net/http/HttpResponse.html)