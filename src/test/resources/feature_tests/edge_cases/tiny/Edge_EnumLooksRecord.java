// Java 5 edge case: Enum with fields that looks like a record
// Test: Enum constructor with fields (predates records)
// Expected Version: 5
// Required Features: ENUMS
enum Edge_EnumLooksRecord {
    RED(255, 0, 0), GREEN(0, 255, 0), BLUE(0, 0, 255);
    final int r, g, b;
    Edge_EnumLooksRecord(int r, int g, int b) { this.r = r; this.g = g; this.b = b; }
}
public class Tiny_EnumLooksRecord_Java5 {}