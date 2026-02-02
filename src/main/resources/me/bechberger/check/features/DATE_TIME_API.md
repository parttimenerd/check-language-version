### Summary

Adds the modern date/time API in [`java.time`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/package-summary.html).

### Details

Java 8 introduced the `java.time` APIs (based on Joda-Time) as a replacement for legacy date/time types like `java.util.Date` and `java.util.Calendar`.

Key types include:

- [`java.time.Instant`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/Instant.html) for a machine timestamp on the UTC time-line
- [`java.time.LocalDate`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/LocalDate.html) and [`java.time.LocalDateTime`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/LocalDateTime.html) for date/time without a time-zone
- [`java.time.ZonedDateTime`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/ZonedDateTime.html) for date/time with a time-zone
- [`java.time.Duration`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/Duration.html) and [`java.time.Period`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/Period.html) for time-based and date-based amounts
- [`java.time.format.DateTimeFormatter`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/format/DateTimeFormatter.html) for parsing/formatting

These types are immutable and thread-safe, and they provide clearer domain modeling than the legacy APIs.

### Example

```java
// Parse a date and compute a future date using java.time
import java.time.LocalDate;
import java.time.Period;

class Example {
    void demo() {
        LocalDate d = LocalDate.parse("2026-02-02"); // ISO-8601 parsing
        LocalDate nextWeek = d.plus(Period.ofDays(7)); // add 7 days
    }
}
```

### Historical

Introduced in Java 8 via JSR 310 as part of the platform work that also added lambdas and streams.

### Links

- [Package java.time (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/package-summary.html)
- [JSR 310: Date and Time API](https://jcp.org/en/jsr/detail?id=310)
- [DateTimeFormatter (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/format/DateTimeFormatter.html)