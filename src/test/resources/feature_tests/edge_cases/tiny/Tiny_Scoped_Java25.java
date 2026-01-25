// Tiny: Scoped values (Java 25)
// Expected Version: 25
// Required Features: GENERICS, LAMBDAS, SCOPED_VALUES

import java.lang.*;

public class Tiny_Scoped_Java25 {
    static final ScopedValue<String> USER = ScopedValue.newInstance();

    void test() {
        ScopedValue.where(USER, "admin").run(() -> {
            System.out.println(USER.get());
        });
    }
}