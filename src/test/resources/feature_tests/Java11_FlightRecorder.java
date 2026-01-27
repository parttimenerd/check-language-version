// Java 11 feature: Flight Recorder API (JEP 328)
// Expected Version: 11
// Required Features: ANNOTATIONS, FLIGHT_RECORDER, NIO, NIO2, TRY_WITH_RESOURCES, TYPE_ANNOTATIONS
import jdk.jfr.*;
import jdk.jfr.consumer.*;

@Name("com.example.MyEvent")
@Label("My Custom Event")
@Description("An example custom JFR event")
@Category({"Example", "Custom"})
public class Java11_FlightRecorder extends Event {
    @Label("Message")
    private String message;

    @Label("Count")
    private int count;

    public Java11_FlightRecorder(String message, int count) {
        this.message = message;
        this.count = count;
    }

    public void testFlightRecorder() {
        // Create and commit an event
        Java11_FlightRecorder event = new Java11_FlightRecorder("Hello JFR", 42);
        event.begin();
        // ... do some work ...
        event.end();
        event.commit();

        // Check if event is enabled
        if (event.isEnabled()) {
            System.out.println("Event is enabled");
        }
    }

    public static void recordingExample() throws Exception {
        // Create a recording
        try (Recording recording = new Recording()) {
            recording.enable("jdk.CPULoad");
            recording.enable("jdk.GCHeapSummary");
            recording.start();

            // Do some work
            Thread.sleep(1000);

            recording.stop();
            recording.dump(java.nio.file.Path.of("recording.jfr"));
        }
    }
}