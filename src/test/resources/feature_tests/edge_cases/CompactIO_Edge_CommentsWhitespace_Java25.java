// Edge case: ensure unqualified IO usage is still detected with comments/whitespace noise.
// Expected Version: 25
// Required Features: COMPACT_SOURCE_FILES, IMPLICITLY_IMPORTED_IO_CLASS, IO_CLASS
// Compile Check: false

void main() {
    // comment before
    /* block comment */
    IO.println(
                    "hi" /* inline */
            );
}