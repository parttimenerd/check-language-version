// Test: ImageIO API (Java 1.4)
// Expected Version: 4
// Required Features: IMAGE_IO
import javax.imageio.ImageIO;
public class Tiny_ImageIO_Java4 {
    void read() throws Exception {
        ImageIO.read(null);
    }
}