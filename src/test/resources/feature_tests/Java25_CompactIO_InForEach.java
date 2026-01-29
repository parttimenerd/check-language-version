// Java 25 feature: Compact source file + default imported IO used in a for-each body
// Expected Version: 25
// Required Features: ALPHA3_ARRAY_SYNTAX, COMPACT_SOURCE_FILES, FOR_EACH, IMPLICITLY_IMPORTED_IO_CLASS, IO_CLASS
// Compile Check: false

int[] xs = {1, 2, 3};

void main() {
    for (int x : xs) {
        IO.println(x);
    }
}