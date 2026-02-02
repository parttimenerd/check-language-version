### Summary


Adds the concurrency utilities in [`java.util.concurrent`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/package-summary.html).

### Details

The [`java.util.concurrent`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/package-summary.html) package provides higher-level concurrency building blocks beyond `synchronized`, `wait/notify`, and raw threads.

Common areas include:

- Thread pools and task execution via [`ExecutorService`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ExecutorService.html) and factories such as [`Executors.newFixedThreadPool(int)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Executors.html#newFixedThreadPool(int))
- Synchronizers like [`CountDownLatch`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CountDownLatch.html) and [`Semaphore`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Semaphore.html)
- Concurrent collections like [`ConcurrentHashMap`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html)
- Atomic variables (lock-free primitives) in [`java.util.concurrent.atomic`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/package-summary.html) such as [`AtomicInteger`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/AtomicInteger.html)

These APIs are the foundation for many scalable concurrent designs in Java and are commonly used in server and application frameworks.

### Example

```java
// Run tasks using an ExecutorService
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Example {
    void demo() {
        ExecutorService pool = Executors.newFixedThreadPool(2); // create a small thread pool
        pool.submit(() -> {
            // do some work in a worker thread
        });
        pool.shutdown(); // initiate an orderly shutdown
    }
}
```

### Historical

Introduced in Java 5 as part of JSR 166, which standardized a large set of concurrency utilities.

### Links

- [Package java.util.concurrent (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/package-summary.html)
- [JSR 166: Concurrency Utilities](https://jcp.org/en/jsr/detail?id=166)
- [ExecutorService (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ExecutorService.html)