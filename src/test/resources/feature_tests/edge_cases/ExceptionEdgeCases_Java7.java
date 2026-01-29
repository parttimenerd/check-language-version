// Edge case: Exception handling evolution
// Expected Version: 7
// Required Features: IO_API, JDBC, MULTI_CATCH, TRY_WITH_RESOURCES
import java.io.*;
import java.sql.*;

class ExceptionEdgeCases_Java7 {

    // Pre-Java 7: Separate catch blocks
    public void testSeparateCatch() {
        try {
            riskyOperation();
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }

    // Java 7: Multi-catch
    public void testMultiCatch() {
        try {
            riskyOperation();
        } catch (IOException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Java 7: Multi-catch with more exception types
    public void testMultiCatchMany() {
        try {
            Class.forName("com.example.Driver");
            riskyOperation();
        } catch (ClassNotFoundException | IOException | SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Java 7: Multi-catch with common supertype
    public void testMultiCatchWithSupertype() throws SQLException {
        try {
            riskyOperation();
        } catch (FileNotFoundException | EOFException e) {
            // Both are IOExceptions, but multi-catch still valid
            System.err.println("File error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Other IO error: " + e.getMessage());
        }
    }

    // Java 7: Rethrowing with improved type checking
    public void testRethrowWithBetterType() throws IOException, SQLException {
        try {
            riskyOperation();
        } catch (Exception e) {
            // Java 7: compiler knows exact types that can be thrown
            System.err.println("Logging: " + e.getMessage());
            throw e;  // Can declare more specific throws clause
        }
    }

    // Java 7: Final exception parameter in multi-catch
    public void testFinalExceptionParam() {
        try {
            riskyOperation();
        } catch (final IOException | SQLException e) {
            // e is implicitly final in multi-catch
            // e = new IOException();  // Would be compile error
            System.err.println(e.getMessage());
        }
    }

    // Combining multi-catch with try-with-resources
    public void testCombinedWithTryResources() {
        try (BufferedReader reader = new BufferedReader(new FileReader("test.txt"))) {
            String line = reader.readLine();
            Integer.parseInt(line);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }

    // Edge case: Mixing multi-catch with regular catch
    public void testMixedCatch() {
        try {
            riskyOperation();
        } catch (IOException | SQLException e) {
            System.err.println("IO or SQL: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Runtime: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Other: " + e.getMessage());
        }
    }

    private void riskyOperation() throws IOException, SQLException {
        throw new SQLException("");
    }
}