// Tiny: Varargs looks like array spread (Java 5)
// Expected Version: 5
// Required Features: VARARGS

class Tiny_VarargsLooksSpread_Java5 {
    void log(String format, Object... args) {}
    void test() { log("{} + {} = {}", 1, 2, 3); }
}