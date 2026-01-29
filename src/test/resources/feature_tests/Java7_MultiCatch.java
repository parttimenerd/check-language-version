// Java 7 feature: Multi-catch
// Expected Version: 7
// Required Features: IO_API, MULTI_CATCH
import java.io.*;

class Java7_MultiCatch {
    public void method() {
        try {
            throw new IOException("test");
        } catch (IOException | RuntimeException e) {
            System.out.println("Caught: " + e.getMessage());
        }
    }
}