### Summary


Allows using `String` expressions in `switch` statements.

### Details

Java 7 added support for `switch` over [`String`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/String.html) values.

This enables using string literals as case labels:

- `case "GET":`
- `case "POST":`

The matching is based on string equality (conceptually using [`String.equals(Object)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/String.html#equals(java.lang.Object))).

### Example

```java
// Switch over strings
class Example {
    int demo(String s) {
        switch (s) {
            case "a":
                return 1;
            case "b":
                return 2;
            default:
                return 0; // fallback
        }
    }
}
```

### Historical

Introduced in Java 7 as part of Project Coin (JSR 334).

### Links

- [JLS §14.11 The switch Statement (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.11)
- [String (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/String.html)
- [JSR 334: Small Enhancements to the Java Programming Language](https://jcp.org/en/jsr/detail?id=334)