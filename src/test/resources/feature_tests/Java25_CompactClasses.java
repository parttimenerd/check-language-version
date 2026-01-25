// Java 25 feature: Compact class declarations (implicitly declared classes)
// Expected Version: 25
// Required Features: COMPACT_SOURCE_FILES
// This file demonstrates a compact/implicit class with just a main method
void main() {
    System.out.println("Hello from a compact class!");

    String message = "This is Java 25";
    print(message);
}

void print(String s) {
    System.out.println(s);
}