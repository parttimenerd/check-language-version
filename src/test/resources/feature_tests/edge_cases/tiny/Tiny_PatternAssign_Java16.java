// Tiny: Pattern instanceof assign (Java 16)
// Expected Version: 16
// Required Features: PATTERN_MATCHING_INSTANCEOF

public class Tiny_PatternAssign_Java16 {
    boolean b = "x" instanceof String s && s.isEmpty();
}