// Test: Unix Domain Sockets (Java 16)
// Expected Version: 16
// Required Features: NIO, UNIX_DOMAIN_SOCKETS
import java.nio.channels.SocketChannel;
import java.net.UnixDomainSocketAddress;
class Tiny_UnixDomainSockets_Java16 {
    void test() throws Exception {
        SocketChannel.open(java.net.StandardProtocolFamily.UNIX);
    }
}