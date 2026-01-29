// Tricky: JNDI lookup looks like modern DI
// Expected Version: 3
// Required Features: JNDI
import javax.naming.InitialContext;

class Tiny_JNDILookup_Java3 {
    Object get(String n) throws Exception {
        return new InitialContext().lookup(n);
    }
}