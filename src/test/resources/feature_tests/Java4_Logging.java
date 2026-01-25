// Java 4 feature: Logging API (java.util.logging)
// Expected Version: 4
// Required Features: IO_API, LOGGING, REFLECTION
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.io.IOException;

public class Java4_Logging {
    // Get a logger for this class
    private static final Logger LOGGER = Logger.getLogger(Java4_Logging.class.getName());

    public void testLogging() {
        // Basic logging at different levels
        LOGGER.severe("This is a severe message");
        LOGGER.warning("This is a warning message");
        LOGGER.info("This is an info message");
        LOGGER.config("This is a config message");
        LOGGER.fine("This is a fine message");
        LOGGER.finer("This is a finer message");
        LOGGER.finest("This is a finest message");

        // Logging with parameters
        LOGGER.log(Level.INFO, "User {0} logged in from {1}", new Object[]{"John", "192.168.1.1"});

        // Logging with exception
        try {
            throw new RuntimeException("Test exception");
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "An error occurred", e);
        }

        // Check if logging level is enabled
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Fine logging is enabled");
        }

        // Set logging level
        LOGGER.setLevel(Level.ALL);
    }

    public void configureLogging() throws IOException {
        // Add console handler
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        LOGGER.addHandler(consoleHandler);

        // Add file handler
        Handler fileHandler = new FileHandler("app.log");
        fileHandler.setLevel(Level.WARNING);
        LOGGER.addHandler(fileHandler);
    }
}