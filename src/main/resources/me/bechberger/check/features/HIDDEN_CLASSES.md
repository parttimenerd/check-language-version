### Summary


Adds hidden classes via [`MethodHandles.Lookup.defineHiddenClass(...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html#defineHiddenClass(byte%5B%5D,boolean,java.lang.invoke.MethodHandles.Lookup.ClassOption...)).

### Details

Hidden classes are classes that cannot be referenced by name from bytecode of other classes.

They are intended for frameworks that generate classes at runtime (for example, dynamic proxies, lambda metafactories, and runtime code generation). Hidden classes:

- are defined from bytecode bytes via a lookup object
- can be unloaded independently when no longer referenced
- may be defined as **strong** or **weak** (affecting how strongly they are tied to the defining class loader)

The primary API is [`MethodHandles.Lookup`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html), which can define hidden classes.

### Example

```java
// Define a hidden class from existing class bytes (illustrative)
import java.lang.invoke.MethodHandles;

class Example {
    Class<?> demo(byte[] bytes) throws Exception {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        // Define the bytes as a hidden class; frameworks typically generate the byte[]
        return lookup.defineHiddenClass(bytes, true).lookupClass();
    }
}
```

### Historical

Introduced in Java 15 via JEP 371 to support JVM languages and frameworks that need runtime-generated classes without polluting the application namespace.

### Links

- [MethodHandles.Lookup.defineHiddenClass(...) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html#defineHiddenClass(byte%5B%5D,boolean,java.lang.invoke.MethodHandles.Lookup.ClassOption...))
- [MethodHandles.Lookup (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html)
- [JEP 371: Hidden Classes](https://openjdk.org/jeps/371)