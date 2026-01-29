// Test: Simple Web Server API (Java 18 - JEP 408)
// Expected Version: 18
// Required Features: SIMPLE_WEB_SERVER
// Optional Features: NIO, NIO2, VAR
import com.sun.net.httpserver.SimpleFileServer;
import java.net.InetSocketAddress;
import java.nio.file.Path;
class Tiny_SimpleWebServer_Java18 {
    void test() {
        // SimpleFileServer is the Java 18 Simple Web Server (JEP 408)
        var server = SimpleFileServer.createFileServer(
            new InetSocketAddress(8000),
            Path.of("."),
            SimpleFileServer.OutputLevel.INFO
        );
    }
}