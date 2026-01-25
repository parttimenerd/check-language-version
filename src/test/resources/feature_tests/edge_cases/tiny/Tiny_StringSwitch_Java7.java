// Test: Strings in switch (Java 7)
// Expected Version: 7
// Required Features: STRINGS_IN_SWITCH
public class Tiny_StringSwitch_Java7 {
    public int test(String s) {
        switch (s) {
            case "a": return 1;
            case "b": return 2;
            default: return 0;
        }
    }
}