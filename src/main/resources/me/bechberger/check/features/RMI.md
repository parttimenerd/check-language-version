### Summary


Adds Remote Method Invocation (RMI) via [`java.rmi`](https://docs.oracle.com/en/java/javase/21/docs/api/java.rmi/java/rmi/package-summary.html) for calling methods on remote objects.

### Details

RMI allows one JVM to invoke methods on an object living in another JVM.

Key pieces include:

- [`Remote`](https://docs.oracle.com/en/java/javase/21/docs/api/java.rmi/java/rmi/Remote.html) marker interface for remote interfaces
- [`RemoteException`](https://docs.oracle.com/en/java/javase/21/docs/api/java.rmi/java/rmi/RemoteException.html) for remote-call failures
- registries via [`java.rmi.registry`](https://docs.oracle.com/en/java/javase/21/docs/api/java.rmi/java/rmi/registry/package-summary.html)

RMI was historically used for distributed Java systems and enterprise applications. Many modern systems use HTTP-based APIs or other RPC stacks instead.

### Example

```java
// Define an RMI remote interface
import java.rmi.Remote;
import java.rmi.RemoteException;

interface HelloService extends Remote {
    String hello(String name) throws RemoteException; // remote call
}
```

### Historical

Introduced in Java 1.1 and widely used in early Java distributed systems. It remains available but is less common in new designs.

### Links

- [Package java.rmi (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.rmi/java/rmi/package-summary.html)
- [Remote (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.rmi/java/rmi/Remote.html)
- [RemoteException (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.rmi/java/rmi/RemoteException.html)