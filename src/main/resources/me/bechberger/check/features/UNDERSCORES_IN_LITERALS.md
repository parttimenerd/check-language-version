### Summary


Allows underscores (`_`) in numeric literals for improved readability.

### Details

Java 7 allows underscores in numeric literals such as `1_000_000`.

Rules (simplified):

- underscores are allowed between digits
- underscores are not allowed at the start or end of the literal
- underscores are not allowed adjacent to a decimal point or type suffix

The underscore has no effect on the numeric value; it is ignored by the compiler.

### Example

```java
// Use underscores to improve readability of a number
class Example {
    long demo() {
        return 1_000_000L; // one million
    }
}
```

### Historical

Introduced in Java 7 as part of Project Coin (JSR 334).

### Links

- [JLS §3.10.1 Integer Literals (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-3.html#jls-3.10.1)
- [JSR 334: Small Enhancements to the Java Programming Language](https://jcp.org/en/jsr/detail?id=334)