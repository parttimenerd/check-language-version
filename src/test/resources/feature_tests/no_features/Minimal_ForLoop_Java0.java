// Java 0 minimal: classic for loop
// Required Features:
// Optional Features:
// Expected Version: 0
// Test: For loop without foreach
class Minimal_ForLoop_Java0 {
    int sum(int n) {
        int s = 0;
        for (int i = 0; i < n; i = i + 1) {
            s = s + i;
        }
        return s;
    }
}