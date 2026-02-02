### Summary


Adds reflection APIs via [`java.lang.Class`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Class.html) and [`java.lang.reflect`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/package-summary.html).

### Details

Reflection allows inspecting classes, methods, fields, and constructors at runtime and (optionally) invoking them.

Common reflective entry points include:

- obtaining a [`Class`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Class.html) object (e.g., `MyType.class` or `obj.getClass()`)
- looking up members via methods like [`Class.getDeclaredMethod(String, Class...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Class.html#getDeclaredMethod(java.lang.String,java.lang.Class...))
- representing members via [`Method`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/Method.html), [`Field`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/Field.html), and [`Constructor`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/Constructor.html)
- invoking methods via [`Method.invoke(Object, Object...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/Method.html#invoke(java.lang.Object,java.lang.Object...))

Reflection is powerful but has trade-offs:

- slower than direct calls
- less compile-time safety
- can be restricted by the module system and security policies

### Example

```java
// Invoke a method reflectively
import java.lang.reflect.Method;

class Example {
    Object demo(Object target) throws Exception {
        Method m = target.getClass().getDeclaredMethod("toString"); // lookup
        return m.invoke(target); // reflective call
    }
}
```

### Historical

Introduced in Java 1.1 and widely used by frameworks (dependency injection, serialization, ORM, testing) to integrate with user code at runtime.

### Links

- [Class (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Class.html)
- [Package java.lang.reflect (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/package-summary.html)
- [Method (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/Method.html)
- [Method.invoke(Object, Object...) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/Method.html#invoke(java.lang.Object,java.lang.Object...))