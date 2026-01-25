// Tiny: Local enum (Java 16)
// Expected Version: 16
// Required Features: ENUMS, LOCAL_ENUMS

public class Tiny_LocalEnumSimple_Java16 {
    void test() {
        enum E { A, B }
        E e = E.A;
    }
}