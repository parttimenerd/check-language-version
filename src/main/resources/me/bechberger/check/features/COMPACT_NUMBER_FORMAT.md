### Summary


Adds compact number formatting via [`NumberFormat.getCompactNumberInstance(...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/text/NumberFormat.html#getCompactNumberInstance(java.util.Locale,java.text.NumberFormat.Style)).

### Details

Compact number formatting displays numbers in a human-friendly abbreviated form, such as "1K"/"1 thousand" or "1M"/"1 million", depending on locale and style.

Java provides this via [`NumberFormat`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/text/NumberFormat.html) using:

- [`NumberFormat.getCompactNumberInstance(Locale, NumberFormat.Style)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/text/NumberFormat.html#getCompactNumberInstance(java.util.Locale,java.text.NumberFormat.Style))
- [`NumberFormat.Style.SHORT`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/text/NumberFormat.Style.html#SHORT) and [`NumberFormat.Style.LONG`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/text/NumberFormat.Style.html#LONG)

The actual output depends on the selected [`Locale`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Locale.html).

### Example

```java
// Format a number in compact notation
import java.text.NumberFormat;
import java.util.Locale;

class Example {
    String demo() {
        NumberFormat f = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
        return f.format(12_300); // e.g., "12K" (locale-dependent)
    }
}
```

### Historical

Introduced in Java 12 as part of locale and i18n improvements.

### Links

- [NumberFormat.getCompactNumberInstance(...) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/text/NumberFormat.html#getCompactNumberInstance(java.util.Locale,java.text.NumberFormat.Style))
- [NumberFormat.Style (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/text/NumberFormat.Style.html)
- [Locale (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Locale.html)