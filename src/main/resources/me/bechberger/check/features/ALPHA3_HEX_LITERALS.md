### Summary

Adds support for hexadecimal literals like `0X1` and `0x1L`.

### Details

HotJava/Java 1.0-alpha3 added compiler support for hex literals with an uppercase `X` prefix (`0X...`) and for using an `L` suffix (`...L`) to indicate a long literal.

Modern Java supports hexadecimal integer literals broadly (including underscores, binary literals later, etc.), but this feature documents that early alpha3 tooling started to accept these forms.

### Example

```java
// Alpha3-era hex literal forms
class Example {
    int a = 0X1;   // uppercase X
    long b = 0x1L; // long suffix
}
```

### Historical

Mentioned as a compiler change in HotJava/Java 1.0-alpha3.

### Links

- [HotJava 1.0 alpha3 changes (compiler fixes: 0X1 and 0x1L)](https://github.com/Marcono1234/HotJava-1.0-alpha/blob/master/hj-alpha3/hotjava/doc/misc/changes.html)