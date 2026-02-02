// Java 8 feature: Java8_RepeatingAnnotations
// Test: Java8_RepeatingAnnotations
// Expected Version: 8
// Required Features: ANNOTATIONS, REPEATING_ANNOTATIONS
import java.lang.annotation.*;

/**
 * Test file for Java 8 Repeating Annotations feature.
 */
@Repeatable(Schedules.class)
@interface Schedule {
    String day();
}

@interface Schedules {
    Schedule[] value();
}

@Schedule(day = "Monday")
@Schedule(day = "Friday")
class Java8_RepeatingAnnotations {

    @Schedule(day = "Tuesday")
    @Schedule(day = "Thursday")
    public void processData() {
        System.out.println("Processing data");
    }
}