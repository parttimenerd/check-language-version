### Summary


Adds the DNS resolution SPI via [`java.net.spi.InetAddressResolverProvider`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/spi/InetAddressResolverProvider.html).

### Details

Java 18 introduced a standard service-provider interface (SPI) that lets applications or libraries plug in a custom name/address resolution implementation.

The SPI lives in the [`java.net.spi`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/spi/package-summary.html) package.

A provider can return an [`InetAddressResolver`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/spi/InetAddressResolver.html) which handles lookups that otherwise flow through [`InetAddress`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/InetAddress.html).

This is primarily intended for environments that need non-default DNS behavior or deep integration with platform/network stacks.

### Example

```java
// Skeleton of an InetAddressResolverProvider (illustrative)
import java.net.spi.InetAddressResolver;
import java.net.spi.InetAddressResolverProvider;

class ExampleProvider extends InetAddressResolverProvider {
    @Override
    public String name() {
        return "example"; // provider name
    }

    @Override
    public InetAddressResolver get(Configuration configuration) {
        // Real implementations return a resolver that performs lookups.
        throw new UnsupportedOperationException("demo");
    }
}
```

### Historical

Introduced in Java 18 via JEP 418 to make DNS resolution pluggable.

### Links

- [InetAddressResolverProvider (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/spi/InetAddressResolverProvider.html)
- [Package java.net.spi (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/spi/package-summary.html)
- [JEP 418: Internet-Address Resolution SPI](https://openjdk.org/jeps/418)