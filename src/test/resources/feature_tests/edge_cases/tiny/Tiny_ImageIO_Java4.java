// Test: ImageIO API (Java 1.4)
// Expected Version: 4
// Required Features: IMAGE_IO, IO_API
import javax.imageio.ImageIO;
import java.io.File;

class Tiny_ImageIO_Java4 {
    void read() throws Exception {
        ImageIO.read(new File("image.png"));
    }
}