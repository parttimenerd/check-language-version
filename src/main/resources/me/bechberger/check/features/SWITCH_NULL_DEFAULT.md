### Summary



Allows handling `null` explicitly in `switch` with `case null` (with pattern matching for switch).

### Details

In modern `switch` (pattern matching), `null` can be handled explicitly with `case null`.

This makes `switch` behavior around `null` values explicit and avoids surprising `NullPointerException` behavior.

### Example

```java
// Handle null explicitly in a switch
class Example {
    int demo(String s) {
        return switch (s) {
            case null -> -1; // explicit null handling
            case "a" -> 1;
            default -> 0;
        };
    }
}
```

### Historical

Standardized in Java 21 as part of pattern matching for switch (JEP 441).

### Links

- [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441)
- [JLS §14.11 The switch Statement (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.11)
- [String (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/String.html)