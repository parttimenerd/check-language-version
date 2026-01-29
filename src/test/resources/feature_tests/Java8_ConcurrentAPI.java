// Java 8 feature: Concurrency utilities
// Expected Version: 8
// Required Features: AUTOBOXING, CONCURRENT_API, DIAMOND_OPERATOR, GENERICS, LAMBDAS
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class Java8_ConcurrentAPI {
    public void testConcurrentAPI() throws Exception {
        // ExecutorService
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Submit Callable
        Future<String> future = executor.submit(() -> {
            Thread.sleep(100);
            return "Hello from callable";
        });

        // Get result
        String result = future.get();
        System.out.println("Result: " + result);

        // Submit Runnable
        executor.execute(() -> System.out.println("Running task"));

        // CountDownLatch
        CountDownLatch latch = new CountDownLatch(3);
        for (int i = 0; i < 3; i++) {
            executor.execute(() -> {
                System.out.println("Task done");
                latch.countDown();
            });
        }
        latch.await(5, TimeUnit.SECONDS);

        // Atomic variables
        AtomicInteger counter = new AtomicInteger(0);
        counter.incrementAndGet();
        counter.compareAndSet(1, 2);

        // ConcurrentHashMap
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("key", 42);
        map.putIfAbsent("key", 100);

        executor.shutdown();
    }
}