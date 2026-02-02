### Summary


Adds `default` methods in interfaces.

### Details

A `default` method is a non-abstract method declared in an interface using the `default` keyword.

Default methods allow interfaces to evolve by adding new methods without breaking existing implementations. An implementing class inherits the default implementation unless it overrides it.

Key rules:

- If two interfaces provide conflicting defaults, the implementing class must resolve the conflict by overriding the method.
- A class method always wins over an interface default ("class wins" rule).

Default methods are used throughout the standard library; for example, [`Iterable.forEach(Consumer)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Iterable.html#forEach(java.util.function.Consumer)) was added as a default method.

### Example

```java
// Define a default method in an interface
interface Greeter {
    default String greet(String name) { // default implementation lives in the interface
        return "Hello " + name;
    }
}

class Example implements Greeter {
    void demo() {
        String s = greet("world"); // inherits the default method
    }
}
```

### Historical

Introduced in Java 8 (JSR 335) primarily to enable adding methods to existing interfaces and to support the Streams API.

### Links

- [Java Language Specification (Java SE 21), 9.4.3 Interface Method Body](https://docs.oracle.com/javase/specs/jls/se21/html/jls-9.html#jls-9.4.3)
- [Iterable.forEach(Consumer) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Iterable.html#forEach(java.util.function.Consumer))
- [JSR 335: Lambda Expressions for the Java Programming Language](https://jcp.org/en/jsr/detail?id=335)