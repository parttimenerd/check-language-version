// Baseline: method reference to System.out.println (no java.io.IO usage)
// Expected Version: 8
// Required Features: METHOD_REFERENCES

class Java0_SystemOut_MethodRef {
    public void demo() {
        Runnable r = System.out::println;
        r.run();
    }
}