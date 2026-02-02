### Summary


Adds `static` methods in interfaces.

### Details

Java 8 allows interfaces to declare `static` methods.

These methods:

- belong to the interface type itself (called as `MyInterface.util()`)
- are not inherited by implementing classes
- can be used to provide factory methods and helpers closely associated with the interface

This is commonly used in the JDK (for example [`Comparator.comparing(...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Comparator.html#comparing(java.util.function.Function))) to provide fluent factory/utility methods.

### Example

```java
// Define and call a static method on an interface
interface ExampleApi {
    static int add(int a, int b) {
        return a + b; // helper owned by the interface
    }
}

class Example {
    int demo() {
        return ExampleApi.add(1, 2); // call via interface name
    }
}
```

### Historical

Introduced in Java 8 together with default methods to support interface evolution and richer APIs.

### Links

- [JLS §9.4 Interface Method Declarations (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-9.html#jls-9.4)
- [Comparator.comparing(Function) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Comparator.html#comparing(java.util.function.Function))
- [JSR 335: Lambda Expressions for the Java Programming Language](https://jcp.org/en/jsr/detail?id=335)