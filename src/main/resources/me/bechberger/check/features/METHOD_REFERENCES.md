### Summary


Adds method references (`::`) as a compact form of certain lambdas.

### Details

A method reference provides a shorthand for a lambda that only calls an existing method or constructor.

Common forms include:

- `Type::staticMethod`
- `instanceExpr::instanceMethod`
- `Type::instanceMethod` (the receiver becomes the first argument)
- `Type::new` (constructor reference)

Method references are used with functional interfaces such as [`Function`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Function.html) and [`Consumer`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Consumer.html).

### Example

```java
// Use a method reference instead of a lambda
import java.util.List;

class Example {
    void demo(List<String> xs) {
        xs.forEach(System.out::println); // equivalent to s -> System.out.println(s)
    }
}
```

### Historical

Introduced in Java 8 (JSR 335) together with lambdas and functional interfaces.

### Links

- [Java Language Specification (Java SE 21), §15.13 Method Reference Expressions](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.13)
- [Package java.util.function (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/package-summary.html)
- [JSR 335: Lambda Expressions for the Java Programming Language](https://jcp.org/en/jsr/detail?id=335)