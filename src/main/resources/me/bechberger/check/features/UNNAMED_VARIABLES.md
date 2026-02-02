### Summary


Adds unnamed variables and unnamed patterns using `_`.

### Details

Unnamed variables and patterns allow you to indicate that a variable is intentionally unused.

This is useful in:

- pattern matching (e.g., record patterns)
- enhanced `switch` patterns
- destructuring-like contexts where not all components are needed

Using `_` improves readability by making ignored components explicit and avoiding dummy names.

### Example

```java
// Ignore a component with an unnamed pattern
record Pair(int a, int b) {}

class Example {
    int demo(Object o) {
        if (o instanceof Pair(int x, _)) {
            return x; // only use the first component
        }
        return 0;
    }
}
```

### Historical

Standardized in Java 22 via JEP 456.

### Links

- [JEP 456: Unnamed Variables & Patterns](https://openjdk.org/jeps/456)
- [JLS §14.11 The switch Statement (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.11)
- [JLS §15.20.2 Type Comparison Operator instanceof (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.20.2)