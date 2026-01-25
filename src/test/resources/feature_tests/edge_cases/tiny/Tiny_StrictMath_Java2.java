// Tricky: strictfp looks like annotation but is Java 1.2
// Expected Version: 2
// Required Features: STRICTFP
public strictfp class Tiny_StrictMath_Java2 {
    double calc() { return 1.0 / 3.0 * 3.0; }
}