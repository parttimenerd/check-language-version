// Test: Multi-catch (Java 7)
// Expected Version: 7
// Required Features: MULTI_CATCH
import java.io.*;
import java.sql.*;
public class Tiny_MultiCatch_Java7 {
    public void test() throws IOException, SQLException {
        try {
            throw new IOException();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }
}