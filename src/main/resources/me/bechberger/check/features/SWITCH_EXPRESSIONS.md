### Summary


Adds switch expressions (`switch` can return a value) with arrow labels and `yield`.

### Details

A switch expression evaluates to a value.

Compared to traditional switch statements, switch expressions:

- can use `->` arrow labels
- can have expression result values per case
- use `yield` to return a value from a block case

Switch expressions help write concise, expression-oriented code and reduce fall-through bugs.

### Example

```java
// Compute a value using a switch expression
class Example {
    int demo(String s) {
        return switch (s) {
            case "a" -> 1;
            case "b" -> 2;
            default -> {
                yield 0; // yield from a block
            }
        };
    }
}
```

### Historical

Standardized in Java 14 via JEP 361 after preview in earlier releases.

### Links

- [JEP 361: Switch Expressions](https://openjdk.org/jeps/361)
- [JLS §15.28 Switch Expressions (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.28)