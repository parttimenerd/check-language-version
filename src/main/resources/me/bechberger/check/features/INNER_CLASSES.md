### Summary


Adds inner classes (member, local, and anonymous) nested inside other classes or blocks.

### Details

Inner classes are classes declared inside another class or inside a block.

Common forms include:

- **Member inner classes**: non-static nested classes that have an implicit reference to the enclosing instance
- **Static nested classes**: declared with `static` and do not capture an enclosing instance
- **Local classes**: declared within a method or block
- **Anonymous classes**: unnamed local classes used for one-off implementations

Inner classes enable better encapsulation (keeping helper types close to where they are used) and were historically important for callback-style APIs (before lambdas).

### Example

```java
// Member inner class captures the outer instance
class Outer {
    private final String prefix = "x";

    class Inner {
        String demo() {
            return prefix + "-inner"; // accesses Outer.this.prefix
        }
    }

    String run() {
        return new Inner().demo(); // create inner class instance
    }
}
```

### Historical

Introduced in Java 1.1 as a major language enhancement that also enabled patterns later used by UI and event-driven APIs.

### Links

- [JLS §8.1.3 Inner Classes and Enclosing Instances (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.1.3)
- [JLS §15.9 Class Instance Creation Expressions (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.9)