// Java 1.0 feature: AWT without inner classes
// Expected Version: 0
// Required Features: AWT
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;

public class Java0_PureAWT {
    public Frame createWindow() {
        Frame frame = new Frame("Simple AWT");
        frame.setSize(300, 200);

        Panel panel = new Panel();
        Label label = new Label("Hello AWT!");

        panel.add(label);
        frame.add(panel);

        return frame;
    }
}