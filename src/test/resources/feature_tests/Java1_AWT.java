// Java 1.0 feature: Abstract Window Toolkit (AWT)
// Expected Version: 1
// Required Features: AWT
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