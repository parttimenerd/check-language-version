// Java 25 combination: Virtual threads + Scoped values
// Test: Combination of virtual threads with scoped values
// Expected Version: 25
// Required Features: SCOPED_VALUES
import java.lang.ScopedValue;
public class Combo_VirtualThreadsScoped_Java25 {
    // Explicit ScopedValue for detection
    private static final ScopedValue<String> USER = ScopedValue.newInstance();

    void test() throws Exception {
        ScopedValue.runWhere(USER, "admin", () -> {
            System.out.println("User: " + USER.get());
        });
    }
}