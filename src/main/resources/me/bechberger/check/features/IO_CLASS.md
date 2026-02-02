### Summary


Adds the convenience I/O API [`java.lang.IO`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/IO.html).

### Details

[`java.lang.IO`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/IO.html) is a standard library convenience API intended to simplify common console and file I/O operations, especially in small programs.

It complements classic `java.io` and `java.nio.file` APIs by providing a simpler surface for typical tasks, such as:

- simple console output (e.g., `IO.print(...)`, `IO.println(...)`)
- reading from standard input (e.g., `IO.readLine(...)`)
- reading and writing files in one call (e.g., `IO.readString(...)`, `IO.writeString(...)`)

### Example

```java
// Use java.io.IO convenience methods
// import java.io.IO; // implicitly available in compact source files
import java.nio.file.Path;
import java.lang.IO;

class Example {
    void demo() throws Exception {
        IO.println("Hello"); // console output

        String name = IO.readLine("Name: "); // prompt and read a line
        IO.println("Hi, " + name);

        Path p = Path.of("hello.txt");
        IO.writeString(p, "Hello " + name + "\n"); // write a text file

        String content = IO.readString(p); // read the whole file back
        IO.println(content);
    }
}
```

### Historical

Introduced in Java 25; initially previewed earlier (the ecosystem rationale is to make small programs easier to write).

### Links

- [java.io.IO (Oracle Javadoc, Java 25)](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/IO.html)
- [JEP 512: Compact Source Files and Instance Main Methods](https://openjdk.org/jeps/512)