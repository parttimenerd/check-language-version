### Summary

Adds Java Flight Recorder (JFR) APIs in [`jdk.jfr`](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.jfr/jdk/jfr/package-summary.html) for low-overhead event recording.

### Details

Java Flight Recorder provides a built-in, low-overhead event recording system for profiling and diagnostics.

The public API lives in [`jdk.jfr`](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.jfr/jdk/jfr/package-summary.html). Common entry points include:

- [`Recording`](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.jfr/jdk/jfr/Recording.html) to control a recording
- [`Event`](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.jfr/jdk/jfr/Event.html) for defining custom events

Recordings can be started/stopped programmatically and written to a file (typically `*.jfr`) for analysis.

### Example

```java
// Start a short JFR recording and dump it to a file
import java.nio.file.Path;
import jdk.jfr.Recording;

class Example {
    void demo(Path out) throws Exception {
        try (Recording r = new Recording()) {
            r.start();                 // start capturing events
            // ... do some work ...
            r.stop();                  // stop capturing
            r.dump(out);               // write recording to a .jfr file
        }
    }
}
```

### Historical

Introduced as a standard feature in Java 11 (JFR had existed earlier in Oracle JDKs, but became generally available as part of OpenJDK).

### Links

- [Package jdk.jfr (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.jfr/jdk/jfr/package-summary.html)
- [Recording (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.jfr/jdk/jfr/Recording.html)
- [JEP 328: Flight Recorder](https://openjdk.org/jeps/328)