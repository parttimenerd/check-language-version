// Java 1.0 feature: Basic I/O API
// Expected Version: 0
// Required Features: IO_API
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class Java0_IO {
    public void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }
}