### Summary


Adds [`HexFormat`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HexFormat.html) for hexadecimal encoding/decoding.

### Details

[`HexFormat`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HexFormat.html) provides a standard way to convert between bytes and their hexadecimal string representation.

It supports configuration options such as:

- upper/lower case output
- delimiters between bytes
- optional `0x` prefix

Common usage includes:

- formatting bytes via [`HexFormat.formatHex(byte[])`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HexFormat.html#formatHex(byte%5B%5D))
- parsing hex via [`HexFormat.parseHex(CharSequence)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HexFormat.html#parseHex(java.lang.CharSequence))

### Example

```java
// Encode and decode bytes using HexFormat
import java.util.HexFormat;

class Example {
    void demo() {
        HexFormat hf = HexFormat.of(); // default hex format (lowercase)
        String s = hf.formatHex(new byte[] { 1, 2, 15 }); // "01020f"
        byte[] b = hf.parseHex(s); // decode back into bytes
    }
}
```

### Historical

Introduced in Java 17 to provide a standard hex utility in the core library.

### Links

- [HexFormat (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HexFormat.html)
- [HexFormat.formatHex(byte[]) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HexFormat.html#formatHex(byte%5B%5D))
- [HexFormat.parseHex(CharSequence) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HexFormat.html#parseHex(java.lang.CharSequence))