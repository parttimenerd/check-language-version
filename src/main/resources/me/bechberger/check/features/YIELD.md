### Summary


Adds `yield` to return a value from a block in a switch expression.

### Details

In a switch expression, an arm can be a block rather than a single expression.

In that case, `yield` is used to provide the value of that switch arm:

- `default -> { ... yield value; }`

This is different from `break`: `break` exits a switch statement, while `yield` produces a value from a switch expression arm.

### Example

```java
// Use yield from a block in a switch expression
class Example {
    int demo(String s) {
        return switch (s) {
            case "a" -> 1;
            case "b" -> 2;
            default -> {
                int len = s.length(); // compute something
                yield 100 + len; // yield a computed value
            }
        };
    }
}
```

### Historical

Standardized in Java 14 together with switch expressions (JEP 361).

### Links

- [JEP 361: Switch Expressions](https://openjdk.org/jeps/361)
- [JLS §15.28 Switch Expressions (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.28)