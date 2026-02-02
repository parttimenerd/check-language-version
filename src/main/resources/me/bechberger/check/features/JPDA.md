### Summary


Adds the Java Platform Debugger Architecture (JPDA) and the Java Debug Interface (JDI) in [`com.sun.jdi`](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.jdi/com/sun/jdi/package-summary.html).

### Details

JPDA is the set of technologies that enable debugging Java applications.

It includes:

- JDWP (Java Debug Wire Protocol) for communication between debugger and target VM
- JDI (Java Debug Interface) for debugger tooling APIs

The JDI APIs in [`com.sun.jdi`](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.jdi/com/sun/jdi/package-summary.html) are typically used by IDEs and debugging tools to attach to a target JVM, inspect threads/frames, set breakpoints, and evaluate expressions.

### Example

```java
// Represent a target JVM in a debugger tool (illustrative)
import com.sun.jdi.VirtualMachine;

class Example {
    VirtualMachine vm; // would be obtained via a Connector to attach/launch
}
```

### Historical

Introduced in Java 1.3 to standardize debugging support and enable tooling such as IDE debuggers.

### Links

- [Package com.sun.jdi (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.jdi/com/sun/jdi/package-summary.html)
- [VirtualMachine (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.jdi/com/sun/jdi/VirtualMachine.html)
- [JPDA overview (Oracle)](https://docs.oracle.com/javase/8/docs/technotes/guides/jpda/)