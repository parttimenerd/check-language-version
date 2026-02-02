### Summary


Adds the diamond operator `<>` for type inference in generic instance creation.

### Details

The diamond operator `<>` lets the compiler infer generic type arguments on the right-hand side of a constructor call.

Instead of repeating type parameters:

- `new ArrayList<String>()`

you can write:

- `new ArrayList<>()`

This improves readability and reduces boilerplate, while preserving static type checking.

### Example

```java
// Use the diamond operator to avoid repeating type arguments
import java.util.ArrayList;
import java.util.List;

class Example {
    List<String> demo() {
        List<String> xs = new ArrayList<>(); // inferred as ArrayList<String>
        xs.add("a");
        return xs;
    }
}
```

### Historical

Introduced in Java 7 as part of Project Coin (small language enhancements).

### Links

- [JLS 15.9 Class Instance Creation Expressions (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.9)
- [ArrayList (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/ArrayList.html)
- [JSR 334: Small Enhancements to the Java Programming Language](https://jcp.org/en/jsr/detail?id=334)