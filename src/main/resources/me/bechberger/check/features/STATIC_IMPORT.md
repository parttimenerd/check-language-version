### Summary


Adds `import static` to import static members (fields and methods) without qualifying them with a type name.

### Details

A static import statement brings static members of a type into scope.

Common forms:

- `import static java.lang.Math.PI;`
- `import static java.lang.Math.*;`

This allows using names like `PI` and `sin(...)` directly, instead of `Math.PI` and `Math.sin(...)`.

Static imports are often used in test code (for example, assertion helpers) or for mathematical constants/functions. Overuse can reduce readability by obscuring where names come from.

### Example

```java
// Use static import for Math constants and methods
import static java.lang.Math.PI;
import static java.lang.Math.sin;

class Example {
    double demo(double x) {
        return sin(x) * PI; // uses imported static members
    }
}
```

### Historical

Introduced in Java 5 as part of the larger set of language enhancements in that release.

### Links

- [JLS §7.5 Import Declarations (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-7.html#jls-7.5)
- [Math (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Math.html)