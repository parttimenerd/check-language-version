### Summary


Adds `enum` types for representing a fixed set of constants.

### Details

An `enum` declaration defines a class whose instances are a fixed set of named constants.

Enums are commonly used for states, options, and categorical values. They provide:

- Type safety (unlike `int` constants)
- A built-in [`Enum`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Enum.html) base class
- Stable names via [`Enum.name()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Enum.html#name()) and ordering via [`Enum.ordinal()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Enum.html#ordinal())

Every enum implicitly has:

- a static method `values()` returning all constants
- a static method `valueOf(String)` to look up a constant by name

Enums can also declare fields, methods, and implement interfaces.

### Example

```java
// Define and use an enum
class Example {
    enum Color {
        RED, GREEN, BLUE; // enum constants
    }

    String demo(Color c) {
        return c.name(); // Enum.name()
    }
}
```

### Historical

Introduced in Java 5 as part of broader language enhancements (commonly associated with JSR 201).

### Links

- [JLS §8.9 Enum Classes (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.9)
- [java.lang.Enum (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Enum.html)
- [JSR 201: Extensions to the Java Programming Language (Java 5)](https://jcp.org/en/jsr/detail?id=201)