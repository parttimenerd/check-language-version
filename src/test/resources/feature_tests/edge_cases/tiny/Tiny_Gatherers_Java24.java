// Test: Stream Gatherers (Java 24)
// Expected Version: 24
// Required Features: STREAM_GATHERERS
import java.util.stream.*;
import java.util.stream.Gatherers;
public class Tiny_Gatherers_Java24 {
    void test() { Stream.of(1,2,3).gather(Gatherers.windowFixed(2)); }
}