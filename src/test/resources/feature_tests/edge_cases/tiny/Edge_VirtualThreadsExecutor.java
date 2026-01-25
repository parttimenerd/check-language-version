// Java 21 edge case: Virtual threads with executor
// Test: Testing virtual threads created through Executors and Thread.ofVirtual()
// Expected Version: 21
// Required Features: VIRTUAL_THREADS
import java.util.concurrent.*;

public class Edge_VirtualThreadsExecutor_Java21 {
    void test() throws Exception {
        // Virtual thread factory
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> System.out.println("Virtual thread task"));
        }

        // Direct virtual thread
        Thread.ofVirtual().start(() -> System.out.println("Virtual thread"));
    }
}