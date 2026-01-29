// Required Features: ALPHA3_TRY_CATCH_FINALLY
class Edge_Alpha3TryCatchFinally {
    void test() {
        try {
            System.out.println("x");
        } catch (RuntimeException e) {
            System.out.println("e");
        } finally {
            System.out.println("y");
        }
    }
}