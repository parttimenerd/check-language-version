### Summary


Adds flexible constructor bodies to allow statements before `this(...)`/`super(...)` constructor invocation.

### Details

Traditionally, a constructor invocation (`this(...)` or `super(...)`) had to be the first statement in a constructor body.

Flexible constructor bodies relax this restriction by allowing certain statements before the explicit constructor invocation, so initial checks or setup code can be written at the top of the constructor.

This is mainly a syntactic and readability improvement, especially for constructors that need validation or early computation before delegating to another constructor.

### Example

```java
// Flexible constructor body (illustrative)
class Example {
    Example(int x) {
        if (x < 0) throw new IllegalArgumentException("x"); // validate before delegation
        this(); // delegate to another constructor
    }

    Example() {
        // default constructor
    }
}
```

### Historical

Introduced in Java 25 via JEP 513.

### Links

- [JEP 513: Flexible Constructor Bodies](https://openjdk.org/jeps/513)
- [Java Language Specification (Java SE 21), §8.8 Constructor Declarations](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.8)
- [IllegalArgumentException (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/IllegalArgumentException.html)