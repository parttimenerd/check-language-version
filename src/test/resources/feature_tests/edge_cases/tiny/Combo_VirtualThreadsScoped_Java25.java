// Java 25 combination: Virtual threads + Scoped values
// Test: Combination of virtual threads with scoped values
// Expected Version: 25
// Required Features: GENERICS, LAMBDAS, SCOPED_VALUES
import java.lang.ScopedValue;
class Combo_VirtualThreadsScoped_Java25 {
    // Explicit ScopedValue for detection
    private static final ScopedValue<String> USER = ScopedValue.newInstance();

    void test() throws Exception {
        ScopedValue.where(USER, "admin").run(() -> {
            System.out.println("User: " + USER.get());
        });
    }
}