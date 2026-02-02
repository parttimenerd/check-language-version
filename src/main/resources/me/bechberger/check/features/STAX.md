### Summary


Adds the Streaming API for XML (StAX) in [`javax.xml.stream`](https://docs.oracle.com/en/java/javase/21/docs/api/java.xml/javax/xml/stream/package-summary.html).

### Details

StAX is a pull-based XML processing API.

Instead of building a full in-memory tree (like DOM), StAX lets you process XML as a stream of events.

Typical entry points:

- [`XMLInputFactory`](https://docs.oracle.com/en/java/javase/21/docs/api/java.xml/javax/xml/stream/XMLInputFactory.html) to create readers
- [`XMLStreamReader`](https://docs.oracle.com/en/java/javase/21/docs/api/java.xml/javax/xml/stream/XMLStreamReader.html) to pull events

This can be more memory efficient for large XML documents.

### Example

```java
// Create an XMLStreamReader (illustrative)
import java.io.StringReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

class Example {
    XMLStreamReader demo(String xml) throws Exception {
        XMLInputFactory f = XMLInputFactory.newFactory();
        return f.createXMLStreamReader(new StringReader(xml)); // pull events from XML
    }
}
```

### Historical

Introduced in Java 6 based on JSR 173 and integrated into the Java SE XML APIs.

### Links

- [Package javax.xml.stream (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.xml/javax/xml/stream/package-summary.html)
- [XMLInputFactory (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.xml/javax/xml/stream/XMLInputFactory.html)
- [JSR 173: Streaming API for XML](https://jcp.org/en/jsr/detail?id=173)