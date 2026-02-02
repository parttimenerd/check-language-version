### Summary


Adds the Java Naming and Directory Interface (JNDI) APIs in [`javax.naming`](https://docs.oracle.com/en/java/javase/21/docs/api/java.naming/javax/naming/package-summary.html).

### Details

JNDI provides a uniform API for naming and directory services.

Typical usage involves:

- creating an [`InitialContext`](https://docs.oracle.com/en/java/javase/21/docs/api/java.naming/javax/naming/InitialContext.html)
- performing lookups via [`Context.lookup(String)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.naming/javax/naming/Context.html#lookup(java.lang.String))

In enterprise environments, JNDI is often used to look up resources such as `DataSource` objects, configuration entries, or directory services.

The APIs support pluggable providers; the actual naming system depends on the environment.

### Example

```java
// Perform a JNDI lookup (environment-dependent)
import javax.naming.Context;
import javax.naming.InitialContext;

class Example {
    Object demo() throws Exception {
        Context ctx = new InitialContext();
        return ctx.lookup("java:comp/env/jdbc/MyDS"); // lookup by name
    }
}
```

### Historical

Introduced in Java 1.3 as a standard abstraction for naming and directory services.

### Links

- [Package javax.naming (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.naming/javax/naming/package-summary.html)
- [InitialContext (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.naming/javax/naming/InitialContext.html)
- [Context.lookup(String) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.naming/javax/naming/Context.html#lookup(java.lang.String))