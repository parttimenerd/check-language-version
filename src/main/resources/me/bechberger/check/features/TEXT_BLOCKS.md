### Summary


Adds text blocks (`"""`) for multi-line string literals.

### Details

A text block is a multi-line string literal delimited by `"""`.

Text blocks improve readability for embedded content such as JSON, XML, SQL, and HTML by:

- allowing multi-line strings without repeated `\n` and concatenation
- handling indentation in a predictable way

### Example

```java
// Create a multi-line string using a text block
class Example {
    String demo() {
        return """
               {
                 "name": "Alice"
               }
               """; // JSON snippet
    }
}
```

### Historical

Standardized in Java 15 via JEP 378 after preview in earlier releases.

### Links

- [JEP 378: Text Blocks](https://openjdk.org/jeps/378)
- [JLS §3.10.6 Text Blocks (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-3.html#jls-3.10.6)