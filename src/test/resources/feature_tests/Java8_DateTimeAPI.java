// Java 8 feature: Java8_DateTimeAPI
// Test: Java8_DateTimeAPI
// Expected Version: 8
// Required Features: DATE_TIME_API
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Test file for Java 8 Date and Time API feature.
 */
class Java8_DateTimeAPI {
    public void testDateTime() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalDateTime dateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatted = dateTime.format(formatter);
        System.out.println("Formatted: " + formatted);
    }
}