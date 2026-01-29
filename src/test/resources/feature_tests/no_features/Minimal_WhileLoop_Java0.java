// Java 0 minimal: while loop
// Required Features:
// Optional Features:
// Expected Version: 0
// Test: While loop with counter
class Minimal_WhileLoop_Java0 {
    int sumTo(int n) {
        int i = 0;
        int s = 0;
        while (i < n) {
            s = s + i;
            i = i + 1;
        }
        return s;
    }
}