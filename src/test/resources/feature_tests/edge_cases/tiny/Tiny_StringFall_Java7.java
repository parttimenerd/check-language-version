// Tiny: String switch fallthrough (Java 7)
// Expected Version: 7
// Required Features: STRINGS_IN_SWITCH

public class Tiny_StringFall_Java7 {
    void test(String s) {
        switch(s) { case "a": case "b": break; }
    }
}