// Java 4 feature: Image I/O API
// Expected Version: 4
// Required Features: AWT, COLLECTIONS_FRAMEWORK, IMAGE_IO, IO_API
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class Java4_ImageIO {
    public void testImageIO() throws IOException {
        // Read an image
        File inputFile = new File("input.png");
        BufferedImage image = ImageIO.read(inputFile);

        if (image != null) {
            System.out.println("Image size: " + image.getWidth() + "x" + image.getHeight());

            // Write image in different format
            File outputFile = new File("output.jpg");
            ImageIO.write(image, "JPEG", outputFile);
            System.out.println("Image saved as JPEG");
        }

        // List available image formats
        String[] readerFormats = ImageIO.getReaderFormatNames();
        System.out.println("Supported read formats:");
        for (int i = 0; i < readerFormats.length; i++) {
            System.out.println("  " + readerFormats[i]);
        }

        String[] writerFormats = ImageIO.getWriterFormatNames();
        System.out.println("Supported write formats:");
        for (int i = 0; i < writerFormats.length; i++) {
            System.out.println("  " + writerFormats[i]);
        }
    }

    public void getImageReaders() throws IOException {
        // Get all readers for a specific format
        Iterator readers = ImageIO.getImageReadersByFormatName("PNG");
        while (readers.hasNext()) {
            ImageReader reader = (ImageReader) readers.next();
            System.out.println("Reader: " + reader.getClass().getName());
        }
    }
}