### Summary

Adds the `assert` statement for runtime-checkable program invariants.

### Details

The `assert` statement checks a boolean condition and throws an `AssertionError` if the condition is false.

Assertions are typically used for **internal invariants** and debugging checks, not for validating user input. They can be enabled or disabled at runtime:

- When disabled (the default in many production setups), assertion expressions are not evaluated.
- When enabled (e.g., with `-ea` / `-enableassertions`), failing assertions throw `AssertionError`.

Java supports two forms:

- `assert condition;`
- `assert condition : message;` where `message` can be any expression (often a `String`).

### Example

```java
// Use assert to document and check an invariant
class Example {
    static int abs(int x) {
        int r = (x >= 0) ? x : -x;
        assert r >= 0 : "abs result must be non-negative"; // enabled with -ea
        return r;
    }
}
```

### Historical

Added in Java 1.4 (JSR 41) as a language feature to support lightweight runtime checks during development and testing.

### Links

- [JLS §14.10 The assert Statement (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.10)
- [JSR 41: A Simple Assertion Facility](https://jcp.org/en/jsr/detail?id=41)
- [java.lang.AssertionError (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/AssertionError.html)