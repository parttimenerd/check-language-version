### Summary


Adds efficient stack walking via [`StackWalker`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/StackWalker.html).

### Details

[`StackWalker`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/StackWalker.html) provides a lazy and efficient way to traverse stack frames.

Compared to older approaches like [`Throwable.getStackTrace()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Throwable.html#getStackTrace()), StackWalker can:

- avoid eagerly materializing the entire stack
- optionally retain class references

Frames are represented as [`StackWalker.StackFrame`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/StackWalker.StackFrame.html).

### Example

```java
// Get the caller class using StackWalker
class Example {
    Class<?> demo() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(s -> s.skip(1).findFirst().orElseThrow().getDeclaringClass());
                // skip(1): skip this demo() frame
    }
}
```

### Historical

Introduced in Java 9 via JEP 259 as part of broader runtime improvements.

### Links

- [StackWalker (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/StackWalker.html)
- [JEP 259: Stack-Walking API](https://openjdk.org/jeps/259)
- [Throwable.getStackTrace() (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Throwable.html#getStackTrace())