// Required Features: ALPHA2_INET_ADDRESS, ALPHA2_SOCKET, ALPHA2_PROTOCOL_EXCEPTION
import net.InetAddress;
import net.Socket;
import net.ProtocolException;

class Edge_Alpha2NetworkingClasses {
    void test() {
        InetAddress a = new InetAddress("localhost");
        Socket s = null;
        if (s == null) {
            throw new ProtocolException("x");
        }
        System.out.println(a);
    }
}