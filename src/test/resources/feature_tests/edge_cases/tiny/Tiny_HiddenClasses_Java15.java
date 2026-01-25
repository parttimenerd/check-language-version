// Test: Hidden Classes API (Java 15)
// Expected Version: 15
// Required Features: HIDDEN_CLASSES
import java.lang.invoke.MethodHandles;
public class Tiny_HiddenClasses_Java15 {
    void test() throws Exception {
        MethodHandles.lookup().defineHiddenClass(new byte[0], true);
    }
}