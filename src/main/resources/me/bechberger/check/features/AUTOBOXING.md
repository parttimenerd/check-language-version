### Summary

Adds autoboxing and unboxing between primitives (e.g., `int`) and wrapper types (e.g., `Integer`).

### Details

Autoboxing automatically converts a primitive value to its corresponding wrapper type, for example:

- `int` → `Integer`
- `boolean` → `Boolean`

Unboxing does the inverse conversion (wrapper → primitive). These conversions are inserted by the compiler and are commonly used with generics and collections, because generics work with reference types, not primitives.

Important behavior and pitfalls:

- Unboxing a `null` reference throws a `NullPointerException`.
- Autoboxing uses wrapper constructors/value-of semantics defined by the platform (e.g., `Integer.valueOf(int)`), which may cache some values.
- Numeric promotions and overload resolution can involve boxing/unboxing and can affect which method overload is chosen.

### Example

```java
// Autoboxing/unboxing in practice
import java.util.ArrayList;
import java.util.List;

class Example {
    void demo() {
        List<Integer> xs = new ArrayList<>();
        xs.add(1);                // autobox: int -> Integer
        int y = xs.get(0);        // unbox: Integer -> int

        Integer n = null;
        // int z = n;             // would throw NullPointerException due to unboxing
    }
}
```

### Historical

Introduced in Java 5 as part of the broader generics/language enhancements (JLS update; commonly associated with JSR 14).

### Links

- [JLS §5.1.7 Boxing Conversion (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-5.html#jls-5.1.7)
- [JLS §5.1.8 Unboxing Conversion (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-5.html#jls-5.1.8)
- [JSR 14: Add Generic Types to the Java Programming Language](https://jcp.org/en/jsr/detail?id=14)