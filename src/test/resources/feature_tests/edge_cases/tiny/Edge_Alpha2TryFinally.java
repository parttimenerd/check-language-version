// Required Features: ALPHA2_TRY_FINALLY
public class Edge_Alpha2TryFinally {
    void test() {
        try {
            System.out.println("x");
        } finally {
            System.out.println("y");
        }
    }
}