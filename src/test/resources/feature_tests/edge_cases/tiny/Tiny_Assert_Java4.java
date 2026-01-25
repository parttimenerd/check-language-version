// Test: Assert keyword (Java 1.4)
// Expected Version: 4
// Required Features: ASSERT
public class Tiny_Assert_Java4 {
    public void test(int x) {
        assert x > 0;
        assert x < 100 : "x must be less than 100";
    }
}