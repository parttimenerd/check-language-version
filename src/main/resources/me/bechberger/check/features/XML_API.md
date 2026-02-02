### Summary


Adds core XML processing APIs (JAXP) in the Java SE platform.

### Details

Java includes standard APIs for parsing and processing XML, primarily via JAXP.

Common parts include:

- DOM parsing via [`javax.xml.parsers.DocumentBuilderFactory`](https://docs.oracle.com/en/java/javase/21/docs/api/java.xml/javax/xml/parsers/DocumentBuilderFactory.html) and [`org.w3c.dom`](https://docs.oracle.com/en/java/javase/21/docs/api/java.xml/org/w3c/dom/package-summary.html)
- SAX parsing via [`org.xml.sax`](https://docs.oracle.com/en/java/javase/21/docs/api/java.xml/org/xml/sax/package-summary.html)
- XSLT transformations via [`javax.xml.transform`](https://docs.oracle.com/en/java/javase/21/docs/api/java.xml/javax/xml/transform/package-summary.html)

These APIs provide implementation-neutral entry points; the actual parser/transformer implementation is selected via JDK defaults or configuration.

### Example

```java
// Parse a small XML document with DOM (illustrative)
import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilderFactory;

class Example {
    Object demo() throws Exception {
        var dbf = DocumentBuilderFactory.newInstance();
        var db = dbf.newDocumentBuilder();
        var doc = db.parse(new ByteArrayInputStream("<root/>".getBytes()));
        return doc.getDocumentElement().getTagName(); // "root"
    }
}
```

### Historical

Standardized in Java 1.4 when XML became a core interoperability format and Java needed built-in parsing and transformation capabilities.

### Links

- [Module java.xml (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.xml/module-summary.html)
- [DocumentBuilderFactory (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.xml/javax/xml/parsers/DocumentBuilderFactory.html)
- [Package org.w3c.dom (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.xml/org/w3c/dom/package-summary.html)
- [Package javax.xml.transform (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.xml/javax/xml/transform/package-summary.html)