### Summary


Adds module import declarations to simplify using modules in source code.

### Details

Module imports allow a source file to import a module and use its exported packages more directly, reducing boilerplate in modular code.

This is intended to improve usability of the module system by making module boundaries and dependencies more explicit in code.

### Example

```java
// Module import (illustrative)
// import module java.base;
class Example {}
```

### Historical

Introduced in Java 25 via JEP 494 as part of continued improvements to the module system.

### Links

- [JEP 494: Module Import Declarations](https://openjdk.org/jeps/494)
- [Java Platform Module System (Oracle)](https://docs.oracle.com/javase/9/migrate/toc.htm)