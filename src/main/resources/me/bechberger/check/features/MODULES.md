### Summary

Adds the Java Platform Module System (JPMS) via `module-info.java` declarations.

### Details

Java modules introduce a strong encapsulation and reliable configuration mechanism for code packaged as modules.

A module is declared in a `module-info.java` file using the `module` keyword and directives like:

- `requires` to depend on another module
- `exports` to make packages accessible to other modules
- `opens` to open packages for deep reflection

At runtime, modules are represented by [`java.lang.Module`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Module.html), and applications can interact with the module graph via APIs like [`Module.getDescriptor()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Module.html#getDescriptor()).

### Example

```java
// A simple module declaration in module-info.java
module com.example.app {
    requires java.logging;  // depend on a standard module
    exports com.example.app; // export an API package
}
```

### Historical

Introduced in Java 9 as part of Project Jigsaw (Java's modularization effort).

### Links

- [java.lang.Module (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Module.html)
- [Java SE 9: The Java Platform Module System (Oracle)](https://docs.oracle.com/javase/9/migrate/toc.htm)
- [Java Language Specification (Java SE 21), §7.7 Module Declarations](https://docs.oracle.com/javase/specs/jls/se21/html/jls-7.html#jls-7.7)