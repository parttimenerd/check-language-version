### Summary

Adds an early form of `try`/`finally` (without `catch`).

### Details

In the 1.0-alpha2 era, Java's exception handling already supported `try`/`finally` blocks, but the combined `try`/`catch`/`finally` form was not yet available. A `finally` block always executes after the `try` block, which is useful for cleanup.

Modern Java supports `try` with `catch` and/or `finally` (and also try-with-resources), but this feature captures the historical point where `finally` existed before the full combined form.

### Example

```java
// Ensure cleanup using try/finally
class Example {
    void demo() {
        Object resource = new Object();
        try {
            // ... use resource ...
        } finally {
            // cleanup runs even if an exception happens above
            resource = null;
        }
    }
}
```

### Links

- [HotJava 1.0 alpha3 changes (mentions combined try/catch/finally)](https://github.com/Marcono1234/HotJava-1.0-alpha/blob/master/hj-alpha3/hotjava/doc/misc/changes.html)