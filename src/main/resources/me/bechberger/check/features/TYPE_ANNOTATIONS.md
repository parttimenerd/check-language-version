### Summary


Adds type-use annotations, allowing annotations to appear on uses of types (not just declarations).

### Details

Type annotations (type-use annotations) allow applying annotations in more places, such as:

- generic type arguments (e.g., `List<@NonNull String>`)
- casts (e.g., `(@TA String) x`)
- `throws` types
- array component types

This is primarily used by static analysis tools (nullness, tainting, immutability) that run at compile time.

The meta-annotation [`@Target`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/Target.html) can include [`ElementType.TYPE_USE`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/ElementType.html#TYPE_USE) to indicate an annotation may be used in type contexts.

### Example

```java
// Define a TYPE_USE annotation and apply it to a type usage
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE_USE)
@interface NonNull {}

class Example {
    @NonNull String demo(@NonNull String s) {
        return s; // illustrative; real tools enforce semantics
    }
}
```

### Historical

Introduced in Java 8 via JSR 308, extending the annotation system to support stronger pluggable type checking.

### Links

- [JLS §9.7.4 Where Annotations May Appear (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-9.html#jls-9.7.4)
- [Target (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/Target.html)
- [ElementType.TYPE_USE (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/ElementType.html#TYPE_USE)
- [JSR 308: Annotations on Java Types](https://jcp.org/en/jsr/detail?id=308)