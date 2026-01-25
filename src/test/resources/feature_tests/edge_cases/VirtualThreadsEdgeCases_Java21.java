// Edge case: Virtual Threads API usage
// Expected Version: 21
// Required Features: VIRTUAL_THREADS
import java.util.concurrent.*;
import java.time.*;
import java.util.*;

public class VirtualThreadsEdgeCases_Java21 {

    // Java 21: Thread.ofVirtual()
    public void testOfVirtual() throws Exception {
        Thread vThread = Thread.ofVirtual()
            .name("my-virtual-thread")
            .start(() -> {
                System.out.println("Running in virtual thread: " + Thread.currentThread());
            });

        vThread.join();
    }

    // Java 21: Thread.startVirtualThread()
    public void testStartVirtualThread() throws Exception {
        Thread vThread = Thread.startVirtualThread(() -> {
            System.out.println("Virtual thread running");
        });

        vThread.join();
    }

    // Java 21: Virtual thread factory
    public void testVirtualThreadFactory() throws Exception {
        ThreadFactory factory = Thread.ofVirtual()
            .name("worker-", 0)
            .factory();

        Thread t1 = factory.newThread(() -> System.out.println("Task 1"));
        Thread t2 = factory.newThread(() -> System.out.println("Task 2"));

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    // Java 21: ExecutorService with virtual threads
    public void testVirtualThreadExecutor() throws Exception {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = new ArrayList<>();

            for (int i = 0; i < 1000; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> {
                    Thread.sleep(100);
                    return "Task " + taskId + " completed";
                }));
            }

            for (Future<String> future : futures) {
                System.out.println(future.get());
            }
        }
    }

    // Java 21: Check if thread is virtual
    public void testIsVirtual() throws Exception {
        Thread virtualThread = Thread.startVirtualThread(() -> {
            boolean isVirtual = Thread.currentThread().isVirtual();
            System.out.println("Is virtual: " + isVirtual);
        });

        Thread platformThread = new Thread(() -> {
            boolean isVirtual = Thread.currentThread().isVirtual();
            System.out.println("Is virtual: " + isVirtual);
        });

        virtualThread.join();
        platformThread.start();
        platformThread.join();
    }

    // Java 21: Platform thread for comparison
    public void testPlatformThread() throws Exception {
        Thread pThread = Thread.ofPlatform()
            .name("my-platform-thread")
            .start(() -> {
                System.out.println("Running in platform thread");
            });

        pThread.join();
    }

    // Combining with var
    public void testWithVar() throws Exception {
        var vThread = Thread.startVirtualThread(() -> {
            System.out.println("Virtual thread with var");
        });

        var factory = Thread.ofVirtual().factory();
        var executor = Executors.newVirtualThreadPerTaskExecutor();

        vThread.join();
        executor.close();
    }
}