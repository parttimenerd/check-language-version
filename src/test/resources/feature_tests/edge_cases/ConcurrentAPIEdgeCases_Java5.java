// Edge case: Concurrent API features across Java versions (uses Java 8 features)
// Expected Version: 8
// Required Features: CONCURRENT_API, FORK_JOIN, LAMBDAS
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.*;

public class ConcurrentAPIEdgeCases_Java5 {

    // Java 5: ExecutorService
    public void testExecutorService() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        ExecutorService single = Executors.newSingleThreadExecutor();
        ExecutorService cached = Executors.newCachedThreadPool();
        ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);

        Future<String> future = executor.submit(() -> "result");
        String result = future.get();

        executor.shutdown();
    }

    // Java 5: Concurrent collections
    public void testConcurrentCollections() {
        ConcurrentHashMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();
        ConcurrentLinkedQueue<String> concurrentQueue = new ConcurrentLinkedQueue<>();
        CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<>();
        CopyOnWriteArraySet<String> cowSet = new CopyOnWriteArraySet<>();

        BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();
        BlockingQueue<String> arrayBlocking = new ArrayBlockingQueue<>(10);
    }

    // Java 5: Atomic classes
    public void testAtomics() {
        AtomicInteger atomicInt = new AtomicInteger(0);
        AtomicLong atomicLong = new AtomicLong(0L);
        AtomicBoolean atomicBool = new AtomicBoolean(false);
        AtomicReference<String> atomicRef = new AtomicReference<>("initial");

        atomicInt.incrementAndGet();
        atomicInt.compareAndSet(1, 2);
        atomicLong.addAndGet(10);
    }

    // Java 5: Locks
    public void testLocks() {
        ReentrantLock lock = new ReentrantLock();
        ReadWriteLock rwLock = new ReentrantReadWriteLock();

        lock.lock();
        try {
            // critical section
        } finally {
            lock.unlock();
        }

        Condition condition = lock.newCondition();
    }

    // Java 5: Synchronizers
    public void testSynchronizers() throws Exception {
        CountDownLatch latch = new CountDownLatch(3);
        CyclicBarrier barrier = new CyclicBarrier(3);
        Semaphore semaphore = new Semaphore(5);

        latch.countDown();
        latch.await();

        barrier.await();

        semaphore.acquire();
        semaphore.release();
    }

    // Java 7: Fork/Join Framework
    public void testForkJoin() {
        ForkJoinPool pool = ForkJoinPool.commonPool();

        RecursiveTask<Integer> task = new RecursiveTask<Integer>() {
            @Override
            protected Integer compute() {
                return 42;
            }
        };

        Integer result = pool.invoke(task);
    }

    // Java 7: Phaser
    public void testPhaser() {
        Phaser phaser = new Phaser(3);
        phaser.arriveAndAwaitAdvance();
        phaser.arriveAndDeregister();
    }

    // Java 8: CompletableFuture
    public void testCompletableFuture() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "hello");

        future.thenApply(String::toUpperCase)
            .thenAccept(System.out::println);

        CompletableFuture<String> combined = future
            .thenCombine(CompletableFuture.supplyAsync(() -> " world"), (a, b) -> a + b);

        CompletableFuture.allOf(future, combined).join();
    }

    // Java 8: StampedLock
    public void testStampedLock() {
        StampedLock lock = new StampedLock();

        long stamp = lock.writeLock();
        try {
            // write operations
        } finally {
            lock.unlockWrite(stamp);
        }

        // Optimistic read
        long readStamp = lock.tryOptimisticRead();
        // read data
        if (!lock.validate(readStamp)) {
            readStamp = lock.readLock();
            try {
                // read data again
            } finally {
                lock.unlockRead(readStamp);
            }
        }
    }

    // Java 9: CompletableFuture enhancements
    public void testCompletableFutureJava9() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "result");

        // completeOnTimeout (Java 9)
        future.completeOnTimeout("timeout", 1, TimeUnit.SECONDS);

        // orTimeout (Java 9)
        future.orTimeout(1, TimeUnit.SECONDS);

        // copy (Java 9)
        CompletableFuture<String> copy = future.copy();
    }
}