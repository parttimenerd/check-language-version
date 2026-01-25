// Test: Varargs (Java 5)
// Expected Version: 5
// Required Features: VARARGS
public class Tiny_Varargs_Java5 {
    public void print(String... args) {
        for (String s : args) System.out.println(s);
    }
}