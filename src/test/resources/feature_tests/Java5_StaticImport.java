// Java 5 feature: Static imports
// Expected Version: 5
// Required Features: STATIC_IMPORT
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

class Java5_StaticImport {
    public double circleArea(double radius) {
        return PI * radius * radius;
    }

    public double diagonal(double a, double b) {
        return sqrt(a * a + b * b);
    }
}