// Java 1.1 feature test: AWT (Java 1.0) with Inner Classes (Java 1.1)
// Expected Version: 1 (due to anonymous inner class for ActionListener)
// Required Features: AWT, INNER_CLASSES
import java.awt.Frame;
import java.awt.Button;
import java.awt.Panel;
import java.awt.Label;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Java1_AWT {
    public void createWindow() {
        Frame frame = new Frame("AWT Example");
        frame.setSize(300, 200);

        Panel panel = new Panel();
        Label label = new Label("Hello AWT!");
        Button button = new Button("Click Me");

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                label.setText("Button clicked!");
            }
        });

        panel.add(label);
        panel.add(button);
        frame.add(panel);

        frame.setVisible(true);
    }
}