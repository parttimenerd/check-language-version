// Tiny: Unnamed variables (Java 22)
// Expected Version: 22
// Required Features: UNNAMED_VARIABLES

public class Tiny_UnnamedSimple_Java22 {
    void test() {
        try { throw new Exception(); }
        catch (Exception _) { }
    }
}