// Tiny: Virtual threads (Java 21)
// Expected Version: 21
// Required Features: LAMBDAS, VIRTUAL_THREADS

public class Tiny_VThread_Java21 {
    void test() {
        Thread.startVirtualThread(() -> {});
    }
}