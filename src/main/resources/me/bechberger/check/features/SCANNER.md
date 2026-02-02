### Summary


Adds [`Scanner`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Scanner.html) for tokenizing and parsing text input.

### Details

[`Scanner`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Scanner.html) can break input into tokens using a delimiter pattern (whitespace by default) and parse tokens into primitive types or strings.

Common methods include:

- [`Scanner.next()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Scanner.html#next())
- [`Scanner.nextInt()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Scanner.html#nextInt())
- [`Scanner.hasNext()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Scanner.html#hasNext())

Scanner is convenient for small inputs and interactive parsing, but it can be slower than buffered parsing for large volumes of data.

### Example

```java
// Read two integers from standard input
import java.util.Scanner;

class Example {
    int demo() {
        Scanner sc = new Scanner(System.in); // tokenizes stdin
        int a = sc.nextInt();
        int b = sc.nextInt();
        return a + b; // sum
    }
}
```

### Historical

Introduced in Java 5 alongside other standard library enhancements.

### Links

- [Scanner (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Scanner.html)
- [Package java.util (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/package-summary.html)