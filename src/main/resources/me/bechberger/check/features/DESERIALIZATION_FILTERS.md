### Summary


Adds deserialization filtering via [`ObjectInputFilter`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/ObjectInputFilter.html) to mitigate unsafe deserialization.

### Details

Java serialization can be risky when reading untrusted data, because deserialization may construct arbitrary object graphs.

Deserialization filters allow applications to validate or reject types and object graph characteristics (like depth and array sizes) during deserialization.

Key APIs include:

- [`ObjectInputFilter`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/ObjectInputFilter.html) for defining a filter
- [`ObjectInputStream.setObjectInputFilter(ObjectInputFilter)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/ObjectInputStream.html#setObjectInputFilter(java.io.ObjectInputFilter)) to apply a filter to a stream
- Global filters via [`ObjectInputFilter.Config.setSerialFilter(ObjectInputFilter)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/ObjectInputFilter.Config.html#setSerialFilter(java.io.ObjectInputFilter))

Filters return a status like `ALLOWED`, `REJECTED`, or `UNDECIDED`.

### Example

```java
// Reject deserialization of all classes except those in a small allowlist
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.IOException;

class Example {
    void demo(ObjectInputStream in) {
        ObjectInputFilter f = info -> {
            Class<?> c = info.serialClass();
            if (c != null && c.getName().startsWith("com.example.")) {
                return ObjectInputFilter.Status.ALLOWED; // allow only our domain objects
            }
            return ObjectInputFilter.Status.REJECTED;
        };
        in.setObjectInputFilter(f); // enforce the filter on this stream
        // in.readObject(); // would apply the filter and may throw IOException/ClassNotFoundException
    }
}
```

### Historical

Introduced in Java 9 as part of broader work to harden serialization. Similar filtering was previously available as a backport in some Java 8 update releases.

### Links

- [ObjectInputFilter (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/ObjectInputFilter.html)
- [ObjectInputStream.setObjectInputFilter(ObjectInputFilter) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/ObjectInputStream.html#setObjectInputFilter(java.io.ObjectInputFilter))
- [JEP 290: Filter Incoming Serialization Data](https://openjdk.org/jeps/290)