// Test: Scoped Values (Java 25) - uses java.lang.ScopedValue
// Expected Version: 25
// Required Features: GENERICS, SCOPED_VALUES
class Tiny_ScopedValues_Java25 {
    static final java.lang.ScopedValue<String> USER = java.lang.ScopedValue.newInstance();
}