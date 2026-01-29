// Subtle Java 25 edge case: compact source file + default-imported IO used in/around a switch
// Expected Version: 25
// Required Features: COMPACT_SOURCE_FILES, IMPLICITLY_IMPORTED_IO_CLASS, IO_CLASS, SWITCH_EXPRESSIONS
// Compile Check: false

void main() {
    int x = 2;
    switch (x) {
        case 1 -> IO.println("one");
        case 2 -> IO.println("two");
        default -> IO.println("other");
    }
}