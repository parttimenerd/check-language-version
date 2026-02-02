### Summary


Adds pattern matching for `switch` (type patterns, `null` handling, and more expressive case labels).

### Details

Pattern matching for `switch` extends `switch` so that case labels can be patterns, not just constants.

Common capabilities include:

- matching on types (type patterns), e.g., `case String s -> ...`
- using a `default` case for everything else

This feature helps write concise, safe dispatch code without chains of `instanceof`/casts.

### Example

```java
// Switch with type patterns
class Example {
    String demo(Object o) {
        return switch (o) {
            case String s -> s.toUpperCase(); // s is a String
            case Integer i -> "int=" + i;
            default -> "other";
        };
    }
}
```

### Historical

Standardized in Java 21 via JEP 441 after preview in earlier releases.

### Links

- [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441)
- [JLS §14.11 The switch Statement (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.11)