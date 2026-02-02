### Summary


Adds JAXB APIs in [`javax.xml.bind`](https://docs.oracle.com/javase/8/docs/api/javax/xml/bind/package-summary.html) for mapping Java objects to/from XML (legacy in the JDK).

### Details

JAXB (Java Architecture for XML Binding) provides a way to serialize Java objects as XML and deserialize XML into objects, typically using annotations on model classes.

Key entry points include:

- [`JAXBContext`](https://docs.oracle.com/javase/8/docs/api/javax/xml/bind/JAXBContext.html) to create marshallers/unmarshallers
- [`Marshaller`](https://docs.oracle.com/javase/8/docs/api/javax/xml/bind/Marshaller.html) to write objects as XML
- [`Unmarshaller`](https://docs.oracle.com/javase/8/docs/api/javax/xml/bind/Unmarshaller.html) to read objects from XML

JAXB was included in the JDK for a time, but the Java EE modules (including JAXB) were removed in Java 11.

### Example

```java
// Create a JAXB marshaller (illustrative; real code needs annotated classes)
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

class Example {
    Marshaller demo(Class<?> type) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(type);
        return ctx.createMarshaller(); // configure formatting/properties as needed
    }
}
```

### Historical

Introduced in Java 6 as part of the included Java EE-related APIs. Removed from the JDK in Java 11 (JEP 320).

### Links

- [Package javax.xml.bind (Oracle Javadoc, Java 8)](https://docs.oracle.com/javase/8/docs/api/javax/xml/bind/package-summary.html)
- [JAXBContext (Oracle Javadoc, Java 8)](https://docs.oracle.com/javase/8/docs/api/javax/xml/bind/JAXBContext.html)
- [JEP 320: Remove the Java EE and CORBA Modules](https://openjdk.org/jeps/320)