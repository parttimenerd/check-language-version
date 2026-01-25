// Tricky: assert looks like modern validation
// Expected Version: 4
// Required Features: ASSERT
public class Tiny_AssertSimple_Java4 {
    void check(int x) {
        assert x > 0 : "must be positive";
    }
}