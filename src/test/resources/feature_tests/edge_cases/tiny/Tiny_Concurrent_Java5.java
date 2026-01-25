// Test: Concurrent API (Java 5)
// Expected Version: 5
// Required Features: CONCURRENT_API
import java.util.concurrent.*;
public class Tiny_Concurrent_Java5 {
    ExecutorService executor = Executors.newFixedThreadPool(4);
    AtomicInteger counter = new AtomicInteger(0);
    ConcurrentHashMap map = new ConcurrentHashMap();
}