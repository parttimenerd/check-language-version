### Summary


Adds the JAX-WS (SOAP Web Services) APIs in [`javax.xml.ws`](https://docs.oracle.com/javase/8/docs/api/javax/xml/ws/package-summary.html) (legacy).

### Details

JAX-WS (Java API for XML Web Services) provides a standard way to build SOAP-based web services and clients.

The API historically lived in `javax.xml.ws` and related packages and supports:

- generating Java stubs from WSDL
- publishing endpoints and creating client proxies

A typical client entry point is [`Service`](https://docs.oracle.com/javase/8/docs/api/javax/xml/ws/Service.html), which can create proxies for a service.

This feature is largely legacy today; the Java EE / JAX-WS modules were removed from the JDK in Java 11.

### Example

```java
// Create a JAX-WS Service (illustrative; real code needs a WSDL URL and QName)
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

class Example {
    Service demo(URL wsdl, QName serviceName) {
        return Service.create(wsdl, serviceName); // create a client service handle
    }
}
```

### Historical

Introduced in Java 6 as part of Java SE's inclusion of web services APIs. Removed from the JDK in Java 11 (JEP 320).

### Links

- [Package javax.xml.ws (Oracle Javadoc, Java 8)](https://docs.oracle.com/javase/8/docs/api/javax/xml/ws/package-summary.html)
- [Service (Oracle Javadoc, Java 8)](https://docs.oracle.com/javase/8/docs/api/javax/xml/ws/Service.html)
- [JEP 320: Remove the Java EE and CORBA Modules](https://openjdk.org/jeps/320)
- [JSR 224: JAX-WS 2.1](https://jcp.org/en/jsr/detail?id=224)