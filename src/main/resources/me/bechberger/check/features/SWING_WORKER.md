### Summary


Adds [`SwingWorker`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/SwingWorker.html) to run background tasks and publish results to the Swing event thread.

### Details

Swing is single-threaded: long-running work on the Event Dispatch Thread (EDT) freezes the UI.

[`SwingWorker`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/SwingWorker.html) helps by:

- running background computation in `doInBackground()` (off the EDT)
- optionally publishing intermediate results
- delivering final results in `done()` (on the EDT)

### Example

```java
// Run background work and update the UI when done
import javax.swing.SwingWorker;

class Example {
    SwingWorker<String, Void> demo() {
        SwingWorker<String, Void> w = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return "result"; // background computation
            }

            @Override
            protected void done() {
                // update UI here (runs on the EDT)
            }
        };
        w.execute(); // start
        return w;
    }
}
```

### Historical

Introduced in Java 6 as a standard helper for doing background work in Swing applications.

### Links

- [SwingWorker (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/SwingWorker.html)
- [Swing concurrency tutorial (Oracle)](https://docs.oracle.com/javase/tutorial/uiswing/concurrency/)