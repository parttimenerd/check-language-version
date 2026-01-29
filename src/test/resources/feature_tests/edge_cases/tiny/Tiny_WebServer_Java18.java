// Tiny: Simple web server (Java 18)
// Expected Version: 18
// Required Features: SIMPLE_WEB_SERVER, NIO, NIO2, VAR

import com.sun.net.httpserver.SimpleFileServer;

class Tiny_WebServer_Java18 {
    void test() throws Exception {
        var server = SimpleFileServer.createFileServer(
            new java.net.InetSocketAddress(8080),
            java.nio.file.Path.of("."),
            SimpleFileServer.OutputLevel.INFO);
    }
}