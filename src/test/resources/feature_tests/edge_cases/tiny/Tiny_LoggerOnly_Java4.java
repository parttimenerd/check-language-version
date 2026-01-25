// Tricky: Logging looks like SLF4J but is Java 1.4
// Expected Version: 4
// Required Features: LOGGING
import java.util.logging.Logger;

public class Tiny_LoggerOnly_Java4 {
    Logger log = Logger.getLogger("test");
}