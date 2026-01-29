// Tiny: Process API (Java 9)
// Expected Version: 9
// Required Features: PROCESS_API

class Tiny_Process_Java9 {
    void test() {
        ProcessHandle ph = ProcessHandle.current();
        long pid = ph.pid();
    }
}