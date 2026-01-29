// Java 25 feature: Compact source files with IO used as a method reference qualifier
// Expected Version: 25
// Required Features: COMPACT_SOURCE_FILES, GENERICS, IMPLICITLY_IMPORTED_IO_CLASS, IO_CLASS, METHOD_REFERENCES

java.util.function.Consumer<String> c = IO::println;

void main() {
    c.accept("mr");
}