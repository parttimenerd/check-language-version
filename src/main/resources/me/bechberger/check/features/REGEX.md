### Summary


Adds regular expression support via [`java.util.regex`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/package-summary.html).

### Details

Java's built-in regular expression engine is provided by the [`java.util.regex`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/package-summary.html) package.

Key types include:

- [`Pattern`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Pattern.html) (a compiled regular expression)
- [`Matcher`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Matcher.html) (stateful matching against input)

Common workflows:

- compile via [`Pattern.compile(String)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Pattern.html#compile(java.lang.String))
- test / search with [`Matcher.find()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Matcher.html#find())
- extract groups with [`Matcher.group(int)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Matcher.html#group(int))

### Example

```java
// Extract a number from a string using regex
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Example {
    int demo(String s) {
        Pattern p = Pattern.compile("id=(\\d+)");
        Matcher m = p.matcher(s);
        return m.find() ? Integer.parseInt(m.group(1)) : -1; // group(1) is the digits
    }
}
```

### Historical

Introduced in Java 1.4 as part of broader standard library additions that also included logging and assertions.

### Links

- [Package java.util.regex (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/package-summary.html)
- [Pattern (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Pattern.html)
- [Matcher (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Matcher.html)