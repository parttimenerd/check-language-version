// Java 1.4 feature: Assert statement
// Expected Version: 4
// Required Features: ASSERT
public class Java4_Assert {
    public void method(int value) {
        assert value > 0 : "Value must be positive";
        assert value < 100;
    }
}