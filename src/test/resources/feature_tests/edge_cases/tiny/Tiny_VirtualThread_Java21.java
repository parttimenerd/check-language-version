// Test: Virtual Threads (Java 21)
// Expected Version: 21
// Required Features: VIRTUAL_THREADS
public class Tiny_VirtualThread_Java21 {
    void test() throws Exception {
        Thread.startVirtualThread(() -> System.out.println("virtual"));
    }
}