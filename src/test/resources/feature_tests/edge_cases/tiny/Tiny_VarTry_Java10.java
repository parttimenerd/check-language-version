// Tiny: Var in try (Java 10)
// Expected Version: 10
// Required Features: IO_API, TRY_WITH_RESOURCES, VAR

class Tiny_VarTry_Java10 {
    void test() throws Exception {
        try (var r = new java.io.StringReader("")) {}
    }
}