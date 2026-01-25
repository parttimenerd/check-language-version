// Tiny: Switch pattern matching (Java 21)
// Expected Version: 21
// Required Features: SWITCH_PATTERN_MATCHING

public class Tiny_SwitchPat_Java21 {
    void test(Object o) {
        switch (o) {
            case String s -> System.out.println(s);
            case Integer i -> System.out.println(i);
            default -> System.out.println("?");
        }
    }
}