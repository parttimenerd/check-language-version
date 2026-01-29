// Baseline: for-each loop with System.out.println (no java.io.IO usage)
// Expected Version: 5
// Required Features: ALPHA3_ARRAY_SYNTAX, FOR_EACH

class Java0_SystemOut_InForEach {
    public void demo(int[] xs) {
        for (int x : xs) {
            System.out.println(x);
        }
    }
}