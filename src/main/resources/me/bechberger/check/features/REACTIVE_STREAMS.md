### Summary


Adds Reactive Streams interfaces via [`java.util.concurrent.Flow`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Flow.html).

### Details

Java 9 introduced the [`Flow`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Flow.html) API as a standard set of interfaces aligned with the Reactive Streams specification.

Key nested interfaces include:

- [`Flow.Publisher<T>`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Flow.Publisher.html)
- [`Flow.Subscriber<T>`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Flow.Subscriber.html)
- [`Flow.Subscription`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Flow.Subscription.html) (backpressure via `request(n)`)
- [`Flow.Processor<T, R>`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Flow.Processor.html)

The protocol is centered around asynchronous item delivery with explicit demand signaling to handle backpressure.

### Example

```java
// A minimal Subscriber skeleton (illustrative)
import java.util.concurrent.Flow;

class Example {
    static class PrintSubscriber implements Flow.Subscriber<String> {
        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            subscription.request(1); // request the first item
        }

        @Override
        public void onNext(String item) {
            System.out.println(item); // handle item
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace(); // handle error
        }

        @Override
        public void onComplete() {
            System.out.println("done"); // completion
        }
    }
}
```

### Historical

Introduced in Java 9 via JEP 266 to standardize Reactive Streams interfaces in the JDK.

### Links

- [Flow (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Flow.html)
- [JEP 266: More Concurrency Updates](https://openjdk.org/jeps/266)
- [Reactive Streams specification](https://www.reactive-streams.org/)