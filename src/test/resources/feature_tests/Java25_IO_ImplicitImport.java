// Java 25 feature: default-imported java.io.IO in compact source files
// Expected Version: 25
// Required Features: IMPLICITLY_IMPORTED_IO_CLASS, IO_CLASS
// Compile Check: false

class Java25_IO_ImplicitImport {
    public void demo() {
        IO.println("hello");
    }
}