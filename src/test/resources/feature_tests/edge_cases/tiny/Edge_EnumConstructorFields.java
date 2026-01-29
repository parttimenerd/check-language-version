// Java 5 edge case: Enum with constructor and fields
// Test: Testing enums with constructors, fields, and methods
// Expected Version: 5
// Required Features: ENUMS
class Edge_EnumConstructorFields {
    enum Planet {
        EARTH(5.976e+24, 6.37814e6),
        MARS(6.421e+23, 3.3972e6);

        private final double mass;
        private final double radius;

        Planet(double mass, double radius) {
            this.mass = mass;
            this.radius = radius;
        }

        double surfaceGravity() {
            return mass / (radius * radius);
        }
    }
}