### Summary


Adds local-variable type inference using `var`.

### Details

`var` lets the compiler infer the static type of a local variable from its initializer.

Key points:

- only for local variables (including `for` loop indexes), not fields or method parameters
- the inferred type is a real static type determined at compile time
- cannot be used without an initializer (because there is nothing to infer)

### Example

```java
// Use var for local-variable type inference
import java.util.ArrayList;

class Example {
    int demo() {
        var xs = new ArrayList<String>(); // inferred type: ArrayList<String>
        xs.add("a");
        return xs.size(); // calls ArrayList.size()
    }
}
```

### Historical

Introduced in Java 10 via JEP 286 to reduce boilerplate while keeping Java statically typed.

### Links

- [JEP 286: Local-Variable Type Inference](https://openjdk.org/jeps/286)
- [JLS §14.4 Local Variable Declarations (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.4)