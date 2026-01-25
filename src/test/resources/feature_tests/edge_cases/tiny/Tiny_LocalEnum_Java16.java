// Test: Local enums (Java 16)
// Expected Version: 16
// Required Features: ENUMS, LOCAL_ENUMS
public class Tiny_LocalEnum_Java16 {
    public void test() {
        enum Color { RED, GREEN, BLUE }
        Color c = Color.RED;
    }
}