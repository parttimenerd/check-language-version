// Subtle: compact source file refers to IO, but declares its own IO -> should NOT claim IO_DEFAULT_IMPORT
// Expected Version: 25
// Required Features: COMPACT_SOURCE_FILES, INNER_CLASSES
// Compile Check: false

class IO {
    static void println(String s) {}
}

void main() {
    IO.println("shadowed");
}