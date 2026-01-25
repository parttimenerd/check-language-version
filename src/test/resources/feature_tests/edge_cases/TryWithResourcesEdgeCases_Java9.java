// Edge case: Try-with-resources variations
// Expected Version: 9
// Required Features: TRY_WITH_RESOURCES, TRY_WITH_EFFECTIVELY_FINAL
import java.io.*;
import java.net.*;
import java.util.zip.*;

public class TryWithResourcesEdgeCases_Java9 {

    // Java 7: Basic try-with-resources with single resource
    public void basicTryWithResources() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("test.txt"))) {
            String line = reader.readLine();
        }
    }

    // Java 7: Multiple resources in same try block
    public void multipleResources() throws IOException {
        try (InputStream in = new FileInputStream("in.txt");
             OutputStream out = new FileOutputStream("out.txt")) {
            in.transferTo(out);
        }
    }

    // Java 9: Effectively final resource (just variable name in try)
    public void effectivelyFinalResource() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("test.txt"));
        try (reader) {
            String line = reader.readLine();
        }
    }

    // Java 9: Mix of effectively final and new resources
    public void mixedResources() throws IOException {
        BufferedReader existing = new BufferedReader(new FileReader("test.txt"));
        try (existing;
             BufferedWriter writer = new BufferedWriter(new FileWriter("out.txt"))) {
            writer.write(existing.readLine());
        }
    }

    // Java 7: Nested try-with-resources
    public void nestedTryWithResources() throws IOException {
        try (FileInputStream fis = new FileInputStream("test.txt")) {
            try (BufferedInputStream bis = new BufferedInputStream(fis)) {
                bis.read();
            }
        }
    }

    // Java 7: Try-with-resources with catch and finally
    public void tryWithCatchFinally() throws IOException {
        try (FileReader fr = new FileReader("test.txt")) {
            fr.read();
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        } finally {
            System.out.println("Cleanup");
        }
    }

    // Java 7: Custom AutoCloseable
    public void customAutoCloseable() {
        try (MyResource res = new MyResource("test")) {
            res.use();
        }
    }

    static class MyResource implements AutoCloseable {
        private final String name;
        MyResource(String name) { this.name = name; }
        void use() { System.out.println("Using " + name); }
        @Override public void close() { System.out.println("Closing " + name); }
    }

    // Java 9: Multiple effectively final resources
    public void multipleEffectivelyFinal() throws IOException {
        InputStream in = new FileInputStream("in.txt");
        OutputStream out = new FileOutputStream("out.txt");
        try (in; out) {
            in.transferTo(out);
        }
    }

    // Java 7: Compressed streams
    public void compressedStreams() throws IOException {
        try (GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream("test.gz"))) {
            gzos.write("Hello".getBytes());
        }
    }

    // Java 7: Network resources
    public void networkResources() throws IOException {
        try (Socket socket = new Socket("localhost", 8080);
             InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {
            out.write("GET / HTTP/1.0\r\n\r\n".getBytes());
        }
    }
}