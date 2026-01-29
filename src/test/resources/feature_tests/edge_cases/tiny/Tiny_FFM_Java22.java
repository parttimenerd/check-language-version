// Tiny: Foreign Function API (Java 22)
// Expected Version: 22
// Required Features: FOREIGN_FUNCTION_API

import java.lang.foreign.*;

class Tiny_FFM_Java22 {
    void test() {
        Arena arena = Arena.ofConfined();
        MemorySegment seg = arena.allocate(100);
    }
}