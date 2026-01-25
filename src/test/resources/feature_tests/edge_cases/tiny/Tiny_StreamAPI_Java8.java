// Test: Stream API (Java 8)
// Expected Version: 8
// Required Features: STREAM_API
import java.util.stream.*;
public class Tiny_StreamAPI_Java8 {
    void test() { Stream.of(1,2,3).filter(x -> x > 1).count(); }
}