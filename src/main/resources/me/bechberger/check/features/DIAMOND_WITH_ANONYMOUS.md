### Summary


Allows using the diamond operator `<>` with anonymous class instance creation.

### Details

Java 7 introduced the diamond operator for generic constructor calls (e.g., `new ArrayList<>()`).

Java 9 extended this so you can also use `<>` when creating an anonymous class, and the compiler can still infer the generic type arguments from the target type.

This reduces boilerplate in code that uses anonymous classes (for example, custom comparators or small adapter implementations) while keeping type safety.

### Example

```java
// Java 9: diamond operator with an anonymous class
import java.util.Comparator;

class Example {
    Comparator<String> demo() {
        return new Comparator<>() { // <> inferred from the target type Comparator<String>
            @Override
            public int compare(String a, String b) {
                return a.length() - b.length(); // compare by length
            }
        };
    }
}
```

### Historical

Introduced in Java 9 as a small language enhancement alongside other incremental improvements.

### Links

- [JLS §15.9 Class Instance Creation Expressions (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.9)
- [Comparator (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Comparator.html)
- [Java 9 language changes overview (Oracle)](https://docs.oracle.com/javase/9/language/toc.htm)