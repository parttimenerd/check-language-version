### Summary


Adds sealed classes and interfaces via `sealed`, `permits`, `non-sealed` to restrict which types may extend/implement a type.

### Details

A `sealed` class or interface restricts which other classes or interfaces may directly extend or implement it.

The permitted subtypes are either:

- listed explicitly using the `permits` clause, or
- inferred from declarations in the same compilation unit (depending on the code layout)

Permitted subclasses must declare one of:

- `final` (cannot be extended further)
- `sealed` (continues restricting subtypes)
- `non-sealed` (removes the restriction for that branch)

Sealed types are often used with pattern matching (especially in `switch`) to model closed hierarchies.

### Example

```java
// Define a closed hierarchy using sealed types
sealed interface Expr permits Const, Add {}

record Const(int value) implements Expr {} // implicitly final

final class Add implements Expr {
    final Expr left;
    final Expr right;
    Add(Expr left, Expr right) {
        this.left = left; // store left
        this.right = right; // store right
    }
}
```

### Historical

Standardized in Java 17 via JEP 409 after preview in earlier releases.

### Links

- [JEP 409: Sealed Classes](https://openjdk.org/jeps/409)
- [JLS §8.1.1.2 Sealed Classes and Interfaces (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.1.1.2)