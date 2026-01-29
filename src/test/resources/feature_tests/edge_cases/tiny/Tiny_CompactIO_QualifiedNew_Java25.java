// Java 25 feature: Compact source files with IO referenced as a qualifier from an anonymous class
// Expected Version: 25
// Required Features: ANNOTATIONS, COMPACT_SOURCE_FILES, IMPLICITLY_IMPORTED_IO_CLASS, INNER_CLASSES, IO_CLASS

Runnable r = new Runnable() {
    @Override public void run() {
        IO.println("anon");
    }
};

void main() {
    r.run();
}