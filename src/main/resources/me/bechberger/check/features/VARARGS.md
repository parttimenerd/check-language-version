### Summary


Adds variable-arity (varargs) methods using `...`.

### Details

A varargs parameter allows a method to accept zero or more arguments of a given type.

Declaring a varargs parameter:

- `void f(int... xs)`

At the call site, you can pass:

- a sequence of arguments: `f(1, 2, 3)`
- an existing array: `f(array)`

At runtime, the varargs parameter is represented as an array.

Varargs are commonly used with formatting APIs such as [`String.format(String, Object...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/String.html#format(java.lang.String,java.lang.Object...)) and utility methods in [`java.util.Arrays`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Arrays.html).

### Example

```java
// A simple varargs method
class Example {
    int sum(int... xs) {
        int s = 0;
        for (int x : xs) {
            s += x; // accumulate
        }
        return s;
    }
}
```

### Historical

Introduced in Java 5 together with generics and other major language enhancements.

### Links

- [JLS §8.4.1 Formal Parameters (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.4.1)
- [String.format(String, Object...) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/String.html#format(java.lang.String,java.lang.Object...))
- [Arrays (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Arrays.html)