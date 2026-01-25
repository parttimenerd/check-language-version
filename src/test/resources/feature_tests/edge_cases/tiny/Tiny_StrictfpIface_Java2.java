// Tiny: Strictfp interface (Java 2)
// Expected Version: 2
// Required Features: STRICTFP

strictfp interface I { double calc(); }

public class Tiny_StrictfpIface_Java2 implements I {
    public double calc() { return 1.0; }
}