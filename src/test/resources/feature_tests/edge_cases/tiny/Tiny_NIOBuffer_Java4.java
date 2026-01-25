// Tricky: NIO Buffer looks modern but is Java 1.4
// Expected Version: 4
// Required Features: NIO
import java.nio.ByteBuffer;

public class Tiny_NIOBuffer_Java4 {
    ByteBuffer buf = ByteBuffer.allocate(1024);
}