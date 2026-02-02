### Summary


Adds record patterns to deconstruct record values in patterns (e.g., `if (o instanceof Point(int x, int y))`).

### Details

A *record pattern* lets you match a record value and bind its components in one step.

This is often described as *deconstruction* of a record. Record patterns integrate with pattern matching contexts such as:

- `instanceof` patterns
- `switch` patterns (when used together with pattern matching for `switch`)

Record patterns improve readability by removing boilerplate component extraction via accessor calls.

### Example

```java
// Deconstruct a record with a record pattern
record Point(int x, int y) {}

class Example {
    int demo(Object o) {
        if (o instanceof Point(int x, int y)) {
            return x + y; // x/y are bound by the pattern
        }
        return 0;
    }
}
```

### Historical

Standardized in Java 21 via JEP 440 as part of Java's broader pattern matching roadmap.

### Links

- [JEP 440: Record Patterns](https://openjdk.org/jeps/440)
- [JLS §14.3 Local Class Declarations (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.3)
- [JLS §8.10 Record Classes (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.10)