// Tiny: Chained calls look like fluent API (Java 1)
// Expected Version: 1
// Required Features: INNER_CLASSES

class Tiny_ChainedLooksFluent_Java1 {
    class Builder {
        Builder a() { return this; }
        Builder b() { return this; }
        Object build() { return null; }
    }
    Object o = new Builder().a().b().build();
}