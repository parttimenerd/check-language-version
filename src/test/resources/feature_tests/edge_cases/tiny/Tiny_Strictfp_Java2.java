// Test: Strictfp keyword (Java 1.2)
// Expected Version: 2
// Required Features: STRICTFP
public strictfp class Tiny_Strictfp_Java2 {
    public strictfp double calculate(double a, double b) {
        return a * b + a / b;
    }
}