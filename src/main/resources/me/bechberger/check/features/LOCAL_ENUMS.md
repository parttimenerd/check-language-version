### Summary


Allows declaring `enum` types locally (e.g., inside a method or block).

### Details

A *local* type declaration is a class/interface/enum/record declared inside a block (commonly inside a method body).

Local enums allow scoping an enum to the smallest relevant region, which can improve readability when the enum is a helper for a specific method.

Like other local types, a local enum is not visible outside the declaring block and can access effectively-final variables from the enclosing scope.

### Example

```java
// Declare an enum inside a method
class Example {
    int demo(String s) {
        enum Mode { A, B } // local enum, visible only in demo()

        Mode m = s.isEmpty() ? Mode.A : Mode.B; // use the local enum
        return m == Mode.A ? 0 : 1;
    }
}
```

### Historical

Introduced in Java 16 as part of work to improve the consistency of local declarations.

### Links

- [JLS §14.3 Local Class Declarations (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.3)
- [JLS §8.9 Enum Classes (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.9)