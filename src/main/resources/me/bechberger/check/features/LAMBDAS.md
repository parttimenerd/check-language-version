### Summary

Adds lambda expressions (`(args) -> expr`) for concise implementations of functional interfaces.

### Details

A lambda expression provides an inline implementation of a **functional interface** (an interface with a single abstract method).

Lambdas are commonly used with standard functional interfaces from [`java.util.function`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/package-summary.html), such as:

- [`Predicate<T>`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Predicate.html)
- [`Function<T, R>`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Function.html)
- [`Consumer<T>`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Consumer.html)

Lambdas can capture effectively-final local variables, and the target type is inferred from context.

### Example

```java
// Use a lambda as a Comparator
import java.util.Comparator;

class Example {
    Comparator<String> byLength() {
        return (a, b) -> a.length() - b.length(); // compare by string length
    }
}
```

### Historical

Introduced in Java 8 (JSR 335) and enabled a broad shift towards functional-style APIs in the JDK (including streams and default methods).

### Links

- [JSR 335: Lambda Expressions for the Java Programming Language](https://jcp.org/en/jsr/detail?id=335)
- [Package java.util.function (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/package-summary.html)
- [Java Language Specification (Java SE 21), §15.27 Lambda Expressions](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.27)