// Tiny: Switch arrow return (Java 14)
// Expected Version: 14
// Required Features: SWITCH_EXPRESSIONS

public class Tiny_SwitchReturn_Java14 {
    int test(int x) { return switch(x) { default -> 0; }; }
}