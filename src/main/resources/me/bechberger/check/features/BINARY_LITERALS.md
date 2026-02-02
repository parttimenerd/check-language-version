### Summary

Adds binary integer literals using the `0b` / `0B` prefix.

### Details

Binary literals let you write integer values directly in base 2 by prefixing the literal with `0b` or `0B`.

They are supported for integral types (`int`, `long`, `short`, `byte`) and are often used for bit masks and flags. Binary literals can be combined with later language features such as underscores in numeric literals (also Java 7) to improve readability.

Binary literals are purely a *source-level* convenience: the compiled class file contains the numeric value, not the original literal spelling.

Related standard library parsing APIs include [`Integer.parseInt(String, int)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Integer.html#parseInt(java.lang.String,int)) and [`Long.parseLong(String, int)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Long.html#parseLong(java.lang.String,int)), which can parse binary strings when given radix `2`.

### Example

```java
// Java 7 binary literals (useful for bit masks)
class Example {
    static final int READ  = 0b100; // 4
    static final int WRITE = 0b010; // 2
    static final int EXEC  = 0b001; // 1

    static boolean canWrite(int perms) {
        return (perms & WRITE) != 0; // bit test
    }
}
```

### Historical

Binary integer literals were introduced in Java 7 (as part of JSR 334, “Project Coin”) together with several small syntax enhancements.

### Links

- [JLS §3.10.1 Integer Literals (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-3.html#jls-3.10.1)
- [JSR 334: Small Enhancements to the Java Programming Language](https://jcp.org/en/jsr/detail?id=334)
- [Integer.parseInt(String, int) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Integer.html#parseInt(java.lang.String,int))