// Test: Local interfaces (Java 16)
// Expected Version: 16
// Required Features: LOCAL_INTERFACES
public class Tiny_LocalInterface_Java16 {
    public void test() {
        interface Local { void run(); }
        Local l = () -> System.out.println("local");
    }
}