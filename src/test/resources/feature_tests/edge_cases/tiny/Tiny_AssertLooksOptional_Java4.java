// Tiny: Assert looks like Optional (Java 4)
// Expected Version: 4
// Required Features: ASSERT

class Tiny_AssertLooksOptional_Java4 {
    String get(Object o) {
        assert o != null : "must not be null";
        return o.toString();
    }
}