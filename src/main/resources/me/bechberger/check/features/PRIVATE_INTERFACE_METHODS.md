### Summary


Adds `private` methods in interfaces to share code between default methods.

### Details

Java 8 introduced default methods, but sharing helper logic between multiple default methods often required duplicating code.

Java 9 added `private` and `private static` methods inside interfaces. These methods are only callable from within the interface itself (for example, from default methods or static methods).

This supports better encapsulation and reduces duplication in interfaces that provide multiple related default methods.

### Example

```java
// Interface with a shared private helper method
interface Greeter {
    default String hello(String name) {
        return prefix() + name; // reuse helper
    }

    default String bye(String name) {
        return prefix() + name + "!"; // reuse helper
    }

    private String prefix() {
        return "Hi "; // private helper, not visible to implementers
    }
}
```

### Historical

Introduced in Java 9 to support cleaner interface design as the standard library continued to evolve interfaces with default methods.

### Links

- [JLS §9.4 Interface Method Declarations (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-9.html#jls-9.4)
- [Java 9 language changes overview (Oracle)](https://docs.oracle.com/javase/9/language/toc.htm)