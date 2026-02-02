### Summary


Adds compact source files and instance main methods for simpler single-file programs.

### Details

Compact source files reduce boilerplate for small programs by allowing a source file without an explicit class declaration, and by supporting instance `main` methods.

This is aimed at teaching, scripts, and small utilities where the full `public class ... { public static void main(String[] args) { ... } }` ceremony can be distracting.

### Example

```java
// A compact source file (no explicit class) with an instance main (illustrative)
void main() {
    System.out.println("Hello"); // simple entry point
}
```

### Historical

Introduced in Java 25 as part of the work on simplifying the Java language for small programs.

### Links

- [JEP 512: Compact Source Files and Instance Main Methods](https://openjdk.org/jeps/512)
- [System.out (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/System.html#out)