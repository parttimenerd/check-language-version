// Test: Reactive Streams (Java 9)
// Expected Version: 9
// Required Features: CONCURRENT_API, GENERICS, REACTIVE_STREAMS
import java.util.concurrent.Flow;
class Tiny_ReactiveStreams_Java9 implements Flow.Subscriber<String> {
    public void onSubscribe(Flow.Subscription s) {}
    public void onNext(String item) {}
    public void onError(Throwable t) {}
    public void onComplete() {}
}