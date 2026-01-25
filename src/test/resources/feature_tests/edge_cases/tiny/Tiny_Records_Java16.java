// Test: Records (Java 16)
// Expected Version: 16
// Required Features: RECORDS
public class Tiny_Records_Java16 {
    record Point(int x, int y) {}
    Point p = new Point(1, 2);
}