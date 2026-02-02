### Summary


Adds JDBC APIs in [`java.sql`](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/package-summary.html) for database connectivity.

### Details

JDBC (Java Database Connectivity) is the standard API for interacting with relational databases from Java.

Core concepts:

- A [`Connection`](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/Connection.html) represents a database session.
- A [`PreparedStatement`](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/PreparedStatement.html) represents a precompiled SQL statement with parameters.
- Results are read via [`ResultSet`](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/ResultSet.html).

Connections are commonly obtained via [`DriverManager.getConnection(String)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/DriverManager.html#getConnection(java.lang.String)) or through a `DataSource` (typically provided by an application server or connection pool).

### Example

```java
// Execute a parameterized query using JDBC
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

class Example {
    int demo() throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:example://localhost/db")) {
            try (PreparedStatement ps = c.prepareStatement("select 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : -1; // read first column
                }
            }
        }
    }
}
```

### Historical

Introduced in Java 1.1 as Java's standard database connectivity layer. JDBC has remained widely used, with later versions adding features such as improved `ResultSet` handling and row-set APIs.

### Links

- [Package java.sql (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/package-summary.html)
- [DriverManager (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/DriverManager.html)
- [Connection (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/Connection.html)