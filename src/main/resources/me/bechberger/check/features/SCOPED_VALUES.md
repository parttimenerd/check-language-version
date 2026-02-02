### Summary


Adds scoped values ([`ScopedValue`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/ScopedValue.html)) for immutable data sharing with callees and child threads.

### Details

Scoped values enable a method to share **immutable** data with its direct and indirect callees within the same thread, and with child threads, without threading extra parameters through every method.

They are intended as an easier-to-reason-about, more structured alternative to [`ThreadLocal`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ThreadLocal.html):

- **No unconstrained mutation:** there is no `set(...)`; data flows one way (caller -> callees).
- **Bounded lifetime:** a binding exists only for the *dynamic scope* of a `run(...)`/`call(...)`.
- **Efficient inheritance:** child threads can inherit bindings without copying, which matters with virtual threads.

A scoped value is typically declared as a capability-like constant:

- `private static final ScopedValue<T> KEY = ScopedValue.newInstance();`

A scoped value is bound for the dynamic extent of an operation by calling:

- `ScopedValue.where(KEY, value).run(() -> { ... })`
- `ScopedValue.where(KEY, value).call(() -> { ... })` (returns a value)

While the operation runs, code can read the value with:

- `KEY.get()`

Bindings can be nested (rebinding the same key for a deeper call chain), and multiple scoped values can be bound at once via chained `where(...)` calls.

Bindings are inherited by child threads created with [`StructuredTaskScope`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/StructuredTaskScope.html), keeping the scope bounded by the lifetime of the parent operation.

### Example

```java
// Use a scoped value to provide a request context to indirect callees
import static java.lang.ScopedValue.where;

import java.lang.ScopedValue;

class Example {
    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    static void handleRequest(String requestId) {
        where(REQUEST_ID, requestId).run(() -> {
            log("start");
            doWork();
        });
        // REQUEST_ID.get() here would fail because the binding is out of scope
    }

    static void doWork() {
        log("work"); // reads REQUEST_ID indirectly
    }

    static void log(String msg) {
        System.out.println(REQUEST_ID.get() + ": " + msg); // scoped data
    }
}
```

### Historical

Finalized in Java 25 via JEP 506 after incubation and multiple preview rounds (JEP 429/446/464/481/487). The finalized API includes a change that `ScopedValue.orElse(...)` no longer accepts `null`.

### Links

- [JEP 506: Scoped Values](https://openjdk.org/jeps/506)
- [ScopedValue (Oracle Javadoc, Java 25)](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/ScopedValue.html)
- [ThreadLocal (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ThreadLocal.html)
- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [JEP 505: Structured Concurrency](https://openjdk.org/jeps/505)
- [StructuredTaskScope (Oracle Javadoc, Java 25)](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/StructuredTaskScope.html)