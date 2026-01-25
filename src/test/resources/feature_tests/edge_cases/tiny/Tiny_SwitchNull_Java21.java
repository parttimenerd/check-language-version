// Tiny: Switch null default (Java 21)
// Expected Version: 21
// Required Features: SWITCH_NULL_DEFAULT

public class Tiny_SwitchNull_Java21 {
    void test(String s) {
        switch (s) {
            case null -> System.out.println("null");
            case "a" -> System.out.println("a");
            default -> System.out.println("other");
        }
    }
}