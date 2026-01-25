// Tricky: Combo of NIO + Logging + assert
// Expected Version: 4
// Required Features: NIO, LOGGING, ASSERT
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class Tiny_NIOLogAssert_Java4 {
    Logger log = Logger.getLogger("x");
    void test() { assert ByteBuffer.allocate(8) != null; }
}