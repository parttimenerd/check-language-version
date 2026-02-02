### Summary

Adds CORBA APIs in [`org.omg.CORBA`](https://docs.oracle.com/javase/8/docs/technotes/guides/idl/corba.html) for distributed objects (legacy).

### Details

CORBA (Common Object Request Broker Architecture) is a standard for language-neutral distributed objects.

Java historically included CORBA APIs under the `org.omg.*` packages so that Java programs could interact with CORBA ORBs and IDL-generated stubs.

The main entry point is typically an [`ORB`](https://docs.oracle.com/javase/8/docs/api/org/omg/CORBA/ORB.html), which provides access to request and type system infrastructure.

This feature is largely legacy today; modern systems typically use other RPC mechanisms.

### Example

```java
// Obtain a CORBA ORB instance (illustrative legacy API)
import org.omg.CORBA.ORB;

class Example {
    ORB demo() {
        return ORB.init(new String[0], null); // initialize ORB with default properties
    }
}
```

### Historical

Introduced in Java 1.2 as part of enterprise/distributed computing support. The CORBA modules and `org.omg.*` APIs were later removed from the JDK in Java 11.

### Links

- [Package org.omg.CORBA (Oracle Javadoc, Java 8)](https://docs.oracle.com/javase/8/docs/api/org/omg/CORBA/package-summary.html)
- [ORB (Oracle Javadoc, Java 8)](https://docs.oracle.com/javase/8/docs/api/org/omg/CORBA/ORB.html)
- [JEP 320: Remove the Java EE and CORBA Modules](https://openjdk.org/jeps/320)