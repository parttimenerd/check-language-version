### Summary

The Applet API for embedding Java programs in web pages (obsolete).

### Details

The `java.applet` package allowed small Java programs (applets) to run inside browsers using the
`<applet>` tag and a plugin-based JVM. Applets ran in a security sandbox and could be signed for extra
privileges. Over time, plugin-based browser models and security concerns made applets impractical.

### Example

```java
// Legacy applet example (browsers no longer support applets)
import java.applet.Applet;
import java.awt.Graphics;

public class HelloApplet extends Applet {
    @Override
    public void paint(Graphics g) { // called by browser plugin
        g.drawString("Hello Applet", 10, 20); // renders text
    }
}
```

### Historical

Introduced early in Java's history to enable interactive web content. Applets declined in use after
security incidents and the removal of NPAPI plugin support in major browsers; the API is deprecated/removed
in modern Java distributions.

### Links

- [Applet Javadoc (Java 8)](https://docs.oracle.com/javase/8/docs/api/java/applet/package-summary.html)
- [Why applets died (Wikipedia)](https://en.wikipedia.org/wiki/Java_applet)