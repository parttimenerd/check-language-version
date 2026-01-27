// Test: For-each loop (Java 5)
// Expected Version: 5
// Required Features: ALPHA3_ARRAY_SYNTAX, FOR_EACH
public class Tiny_ForEach_Java5 {
    public void test() {
        int[] arr = {1, 2, 3};
        for (int n : arr) System.out.println(n);
    }
}