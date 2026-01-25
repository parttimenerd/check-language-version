// Tiny: Hidden classes (Java 15)
// Expected Version: 15
// Required Features: HIDDEN_CLASSES

import java.lang.invoke.*;

public class Tiny_Hidden_Java15 {
    void test() throws Exception {
        MethodHandles.Lookup l = MethodHandles.lookup();
        // defineHiddenClass is the Java 15 API
        byte[] bytes = new byte[0];
        // l.defineHiddenClass(bytes, true);
    }
}