// Java 1.3 feature: JNDI (Java Naming and Directory Interface)
// Expected Version: 3
// Required Features: JNDI
// Compile Check: true
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

class Java3_JNDI {
    public Object lookup(String name) throws NamingException {
        Context ctx = new InitialContext();
        return ctx.lookup(name);
    }
}