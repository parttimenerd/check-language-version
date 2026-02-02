### Summary

Adds the Base64 encoding/decoding API in `java.util.Base64`.

### Details

Java 8 introduced `java.util.Base64`, a standard API for Base64 encoding and decoding binary data.

The class provides different variants via factory methods:

- [`Base64.getEncoder()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Base64.html#getEncoder()) / [`Base64.getDecoder()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Base64.html#getDecoder()) for basic Base64
- [`Base64.getUrlEncoder()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Base64.html#getUrlEncoder()) / [`Base64.getUrlDecoder()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Base64.html#getUrlDecoder()) for URL- and filename-safe Base64
- [`Base64.getMimeEncoder()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Base64.html#getMimeEncoder()) / [`Base64.getMimeDecoder()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Base64.html#getMimeDecoder()) for MIME-friendly output (line-wrapped)

This API avoids the need for non-standard or internal Base64 utilities and provides consistent, well-tested behavior.

### Example

```java
// Encode and decode a string using java.util.Base64
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class Example {
    void demo() {
        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        String encoded = Base64.getEncoder().encodeToString(data); // aGVsbG8=

        byte[] decoded = Base64.getDecoder().decode(encoded);
        String back = new String(decoded, StandardCharsets.UTF_8); // "hello"
    }
}
```

### Historical

Added in Java 8 as part of the standard library to provide Base64 support directly in the JDK.

### Links

- [java.util.Base64 (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Base64.html)
- [Java 8 API documentation overview](https://docs.oracle.com/javase/8/docs/api/overview-summary.html)