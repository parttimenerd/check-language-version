### Summary


Adds the `strictfp` modifier for strict IEEE 754 floating-point behavior.

### Details

The `strictfp` modifier historically controlled whether floating-point intermediate results were allowed to use an extended exponent range on some platforms.

- In older JVMs and on some hardware (notably the x87 floating-point unit), intermediate computations could keep extra range/precision, which could change where overflows/underflows happened.
- Applying `strictfp` to a class, interface, or method forced *strict* floating-point evaluation so results were more predictable across platforms.

In modern Java, `strictfp` is effectively redundant: Java 17 restored always-strict floating-point semantics for all code, so the modifier no longer changes runtime behavior.

### Example

```java
// strictfp is historically used to force strict floating-point semantics
class Example {
    strictfp double demo(double a, double b) {
        return a / b; // on Java 17+, strictfp is redundant
    }
}
```

### Historical

Introduced in Java 1.2 to provide predictable floating-point behavior across platforms. Java 17 restored always-strict floating-point semantics, making `strictfp` redundant (JEP 306).

### Links

- [JEP 306: Restore Always-Strict Floating-Point Semantics](https://openjdk.org/jeps/306)
- [JLS §4.2 Floating-Point Types and Values (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html#jls-4.2)
- [JLS §8.4.3 Method Modifiers (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.4.3)
- [JLS §15.4 FP-strict Expressions (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.4)