### Summary


Adds Java object serialization via [`java.io.Serializable`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/Serializable.html).

### Details

Java serialization supports converting an object graph into a byte stream and reconstructing it later.

Core APIs include:

- marker interface [`Serializable`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/Serializable.html)
- writing via [`ObjectOutputStream`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/ObjectOutputStream.html)
- reading via [`ObjectInputStream`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/ObjectInputStream.html)

Serialization is convenient for persistence and remote communication, but it must not be used with untrusted input because deserialization can be unsafe. Java 9 introduced deserialization filters (`ObjectInputFilter`) to help mitigate risks.

### Example

```java
// Serialize and deserialize an object (illustrative)
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class Example {
    static class Data implements Serializable {
        final String value;
        Data(String value) { this.value = value; }
    }

    Object demo() throws Exception {
        byte[] bytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(new Data("x")); // write object graph
            bytes = baos.toByteArray();
        }

        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return in.readObject(); // read object graph
        }
    }
}
```

### Historical

Introduced in Java 1.1 and used widely in the early Java ecosystem (including RMI). Over time, security concerns and alternative formats (JSON/Protobuf) made it less recommended for new designs.

### Links

- [Serializable (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/Serializable.html)
- [ObjectOutputStream (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/ObjectOutputStream.html)
- [ObjectInputStream (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/ObjectInputStream.html)
- [ObjectInputFilter (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/ObjectInputFilter.html)