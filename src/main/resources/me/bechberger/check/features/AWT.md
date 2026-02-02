### Summary

The Abstract Window Toolkit (AWT), Java's original GUI toolkit.

### Details

AWT provides basic windowing and UI components in package `java.awt`. AWT components are thin wrappers
around native widgets on each platform which historically caused inconsistent look-and-feel and layout
behavior across platforms. Higher-level toolkits (Swing, JavaFX) were introduced later to provide richer,
consistent UI components.

### Example

```java
// Create a simple AWT Frame and show it (illustrative, not headless-safe)
import java.awt.Frame;
import java.awt.Label;

public class AwtExample {
    public static void main(String[] args) {
        Frame f = new Frame("Hello AWT"); // AWT top-level window
        f.add(new Label("AWT example"));  // add a component
        f.setSize(300, 120);
        f.setVisible(true);
    }
}
```

### Historical

Introduced in Java 1.0 as the first GUI toolkit for Java. Swing (since Java 1.2) built on top of/alongside
AWT and offered a more consistent cross-platform UI. AWT remains relevant for low-level native integration
and some legacy codebases.

### Links

- [Oracle AWT Javadoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/java/awt/package-summary.html)
- [AWT on Wikipedia](https://en.wikipedia.org/wiki/Abstract_Window_Toolkit)