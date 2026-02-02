### Summary


Adds Swing, Java's lightweight GUI toolkit, in [`javax.swing`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/package-summary.html).

### Details

Swing is a GUI toolkit built on top of AWT.

It provides:

- lightweight components (painted by Java rather than native widgets)
- a pluggable look and feel
- the single-threaded event dispatch model (updates happen on the AWT Event Dispatch Thread)

Common top-level and widget classes include:

- [`JFrame`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/JFrame.html)
- [`JButton`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/JButton.html)
- [`SwingUtilities`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/SwingUtilities.html) (helpers like `invokeLater`)

### Example

```java
// Create a simple Swing window (illustrative)
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

class Example {
    void demo() {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Hello Swing");
            f.add(new JButton("Click")); // add a component
            f.setSize(300, 120);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
        });
    }
}
```

### Historical

Introduced in Java 1.2 as a richer GUI toolkit than AWT. It remains widely supported for desktop applications, though many newer applications use JavaFX or other UI toolkits.

### Links

- [Package javax.swing (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/package-summary.html)
- [JFrame (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/JFrame.html)
- [SwingUtilities.invokeLater(Runnable) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/SwingUtilities.html#invokeLater(java.lang.Runnable))