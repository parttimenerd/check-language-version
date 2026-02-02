### Summary


Adds generics (`List<String>`) for compile-time type safety with parameterized types.

### Details

Generics allow classes, interfaces, and methods to be parameterized with types (type parameters like `T`). This enables stronger compile-time checks and reduces the need for casts.

A typical example is a typed collection:

- [`List<String>`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html)

Key points:

- Generic type information is enforced by the compiler.
- Java implements generics via *type erasure*, meaning most generic type parameters are not available at runtime.
- Wildcards like `? extends T` and `? super T` express covariance/contravariance in APIs.

Generics are used heavily throughout the standard library (e.g., `java.util` collections) and enable many modern Java APIs.

### Example

```java
// Use generics to avoid casts
import java.util.ArrayList;
import java.util.List;

class Example {
    String demo() {
        List<String> xs = new ArrayList<>(); // List<String>
        xs.add("a");
        return xs.get(0); // no cast needed
    }
}
```

### Historical

Introduced in Java 5 via JSR 14 and integrated throughout the standard library.

### Links

- [JLS §4.5 Parameterized Types (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html#jls-4.5)
- [JLS §4.8 Raw Types (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html#jls-4.8)
- [JSR 14: Add Generic Types to the Java Programming Language](https://jcp.org/en/jsr/detail?id=14)