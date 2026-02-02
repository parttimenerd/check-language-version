### Summary


Adds pattern matching for `instanceof` with a binding variable (e.g., `if (o instanceof String s)`).

### Details

Pattern matching for `instanceof` lets you combine a type test and a cast.

Instead of:

- `if (o instanceof String) { String s = (String) o; ... }`

you can write:

- `if (o instanceof String s) { ... }`

The binding variable (`s`) is in scope only where the pattern is known to match.

### Example

```java
// instanceof with a binding variable
class Example {
    int demo(Object o) {
        if (o instanceof String s) {
            return s.length(); // s is already a String
        }
        return -1;
    }
}
```

### Historical

Standardized in Java 16 after preview in earlier releases, as part of broader efforts to improve pattern matching in Java.

### Links

- [JLS §15.20.2 Type Comparison Operator instanceof (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.20.2)
- [JEP 394: Pattern Matching for instanceof](https://openjdk.org/jeps/394)