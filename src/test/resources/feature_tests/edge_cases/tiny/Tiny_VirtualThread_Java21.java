// Test: Virtual Threads (Java 21)
// Expected Version: 21
// Required Features: LAMBDAS, VIRTUAL_THREADS
class Tiny_VirtualThread_Java21 {
    void test() throws Exception {
        Thread.startVirtualThread(() -> System.out.println("virtual"));
    }
}