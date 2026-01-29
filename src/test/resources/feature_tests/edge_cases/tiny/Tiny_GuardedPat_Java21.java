// Tiny: Guarded pattern (Java 21)
// Expected Version: 21
// Required Features: SWITCH_EXPRESSIONS, SWITCH_PATTERN_MATCHING

class Tiny_GuardedPat_Java21 {
    void test(Object o) {
        switch(o) {
            case String s when s.isEmpty() -> {}
            default -> {}
        }
    }
}