### Summary


Allows declaring `interface` types locally (e.g., inside a method or block).

### Details

A local interface is an interface declared inside a block (often in a method body). It can be used to scope a small abstraction to a specific implementation site.

Local interfaces are not visible outside their declaring block. Like other local types, they can reference effectively-final local variables from the enclosing context.

### Example

```java
// Declare an interface inside a method
class Example {
    String demo(String in) {
        interface Normalizer {
            String normalize(String s); // local interface, visible only in demo()
        }

        Normalizer n = s -> s.trim(); // lambda implements the functional interface
        return n.normalize(in);
    }
}
```

### Historical

Introduced in Java 16 to make local declarations more uniform.

### Links

- [JLS §14.3 Local Class Declarations (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.3)
- [JLS §9.1 Interface Declarations (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-9.html#jls-9.1)