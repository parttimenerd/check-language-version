// Test: Repeating annotations (Java 8)
// Expected Version: 8
// Required Features: ANNOTATIONS, REFLECTION, REPEATING_ANNOTATIONS
import java.lang.annotation.*;
public class Tiny_RepeatingAnnotations_Java8 {
    @Repeatable(Schedules.class)
    @interface Schedule { String day(); }
    @interface Schedules { Schedule[] value(); }

    @Schedule(day="Mon") @Schedule(day="Tue")
    void meeting() {}
}