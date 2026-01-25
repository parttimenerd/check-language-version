// Java 21 feature: Virtual Threads (JEP 444)
// Expected Version: 21
// Required Features: LAMBDAS, VAR, VIRTUAL_THREADS
public class Java21_VirtualThreads {
    public void method() throws Exception {
        // Create virtual thread using Thread.ofVirtual()
        Thread virtualThread = Thread.ofVirtual()
            .name("my-virtual-thread")
            .start(() -> {
                System.out.println("Running in virtual thread: " + Thread.currentThread());
            });

        virtualThread.join();

        // Start virtual thread directly
        Thread.startVirtualThread(() -> {
            System.out.println("Another virtual thread");
        });

        // Create virtual thread factory
        var factory = Thread.ofVirtual().factory();
        Thread t = factory.newThread(() -> System.out.println("From factory"));
        t.start();
        t.join();

        // Check if thread is virtual
        System.out.println("Is virtual: " + Thread.currentThread().isVirtual());
    }
}