### Summary


Adds Image I/O APIs via [`javax.imageio`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/imageio/package-summary.html) (e.g., [`ImageIO`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/imageio/ImageIO.html)).

### Details

The Image I/O APIs provide a standard way to read and write common image formats.

The central utility class is [`ImageIO`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/imageio/ImageIO.html), which supports:

- reading images via [`ImageIO.read(File)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/imageio/ImageIO.html#read(java.io.File)) and related overloads
- writing images via [`ImageIO.write(RenderedImage, String, File)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/imageio/ImageIO.html#write(java.awt.image.RenderedImage,java.lang.String,java.io.File))

Image data is represented using standard AWT image types like [`BufferedImage`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/java/awt/image/BufferedImage.html).

### Example

```java
// Read and write an image using ImageIO
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

class Example {
    void demo(File in, File out) throws Exception {
        BufferedImage img = ImageIO.read(in); // decode image
        ImageIO.write(img, "png", out);      // encode as PNG
    }
}
```

### Historical

Introduced in Java 1.4 as part of the desktop APIs to provide standardized image codecs and pluggable readers/writers.

### Links

- [Package javax.imageio (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/imageio/package-summary.html)
- [ImageIO (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/imageio/ImageIO.html)
- [BufferedImage (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/java/awt/image/BufferedImage.html)