// Tiny: Enum with method looks like record (Java 5)
// Expected Version: 5
// Required Features: ENUMS

enum Color {
    RED(255, 0, 0), GREEN(0, 255, 0), BLUE(0, 0, 255);
    final int r, g, b;
    Color(int r, int g, int b) { this.r = r; this.g = g; this.b = b; }
}

class Tiny_EnumLooksRecord_Java5 {}