// Tiny: Hidden classes (Java 15)
// Expected Version: 15
// Required Features: HIDDEN_CLASSES

import java.lang.invoke.*;
import java.lang.invoke.MethodHandles.Lookup.ClassOption;

public class Tiny_Hidden_Java15 {
    void test() throws Exception {
        MethodHandles.Lookup l = MethodHandles.lookup();
        byte[] bytes = new byte[0];
        // Use Java 15 defineHiddenClass with ClassOption
        l.defineHiddenClass(bytes, true, ClassOption.NESTMATE);
    }
}