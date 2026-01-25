// Tricky: Looks modern but only uses Java 1.0 IO
// Expected Version: 0
// Required Features: IO_API
import java.io.*;

public class Tiny_IOStream_Java0 {
    int read(InputStream in) throws IOException {
        return in.read();
    }
}