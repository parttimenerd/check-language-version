// Tiny: Multi-catch order (Java 7)
// Expected Version: 7
// Required Features: MULTI_CATCH

public class Tiny_MultiCatchOrder_Java7 {
    void test() {
        try { throw new Exception(); }
        catch (RuntimeException | Error e) {}
        catch (Exception e) {}
    }
}