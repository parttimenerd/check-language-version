### Summary

Adds class literals using the `.class` property (e.g., `String.class`) to get a `Class` object.

### Details

A class literal like `String.class` evaluates to a [`Class`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Class.html) object representing the named type.

This is commonly used for:

- Accessing class metadata (e.g., [`Class.getName()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Class.html#getName()))
- Looking up resources relative to a class (e.g., [`Class.getResourceAsStream(String)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Class.html#getResourceAsStream(java.lang.String)))
- Supplying `Class` tokens to APIs (e.g., reflection and serialization frameworks)

Class literals work for primitive types too (e.g., `int.class`) and for array types (e.g., `String[].class`).

### Example

```java
// Using `.class` to get a Class token
class Example {
    void demo() {
        Class<String> c = String.class;      // class literal
        String name = c.getName();           // java.lang.Class#getName
        boolean prim = int.class.isPrimitive(); // primitive class literal
    }
}
```

### Historical

The `.class` class-literal syntax has been part of Java since Java 1.1, as a convenient way to obtain a `Class` object without calling [`Class.forName(String)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Class.html#forName(java.lang.String)).

### Links

- [JLS §15.8.2 Class Literals (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.8.2)
- [java.lang.Class (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Class.html)
- [Class.getResourceAsStream(String) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Class.html#getResourceAsStream(java.lang.String))