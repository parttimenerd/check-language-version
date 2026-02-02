### Summary


Allows multiple labels per `case` using commas (e.g., `case "a", "b" -> ...`).

### Details

With the modernized `switch` syntax, you can specify multiple labels for a single case, separated by commas.

This works with both switch statements and switch expressions and reduces duplicated code:

- `case "GET", "HEAD" -> ...`

### Example

```java
// Multiple labels in a switch
class Example {
    int demo(String s) {
        return switch (s) {
            case "a", "b" -> 1; // two labels
            default -> 0;
        };
    }
}
```

### Historical

Standardized in Java 14 as part of the switch expression work (JEP 361).

### Links

- [JEP 361: Switch Expressions](https://openjdk.org/jeps/361)
- [JLS §15.28 Switch Expressions (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.28)