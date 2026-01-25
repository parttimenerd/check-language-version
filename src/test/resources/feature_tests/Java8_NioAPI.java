// Java 8 feature: NIO.2 API
// Expected Version: 8
// Required Features: NIO2
import java.nio.file.*;
import java.nio.charset.*;
import java.io.*;
import java.util.*;

public class Java8_NioAPI {
    public void method() throws IOException {
        // Path API
        Path path = Paths.get("test.txt");

        // Files API
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        Files.write(path, lines);

        // Walk file tree
        try (Stream<Path> stream = Files.walk(Paths.get("."))) {
            stream.filter(Files::isRegularFile)
                  .forEach(System.out::println);
        }
    }
}