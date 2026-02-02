### Summary

Adds `StringBufferPrintStream` for capturing printed output into a `StringBuffer`.

### Details

HotJava/Java 1.0-alpha3 added `java.io.StringBufferPrintStream`, a convenience `PrintStream`-like class that writes output into a `StringBuffer`.

In modern Java, similar behavior is commonly achieved using `ByteArrayOutputStream` + `PrintStream`, or by using writers like `StringWriter`.


### Links

- [HotJava 1.0 alpha3 changes (added java.io classes including StringBufferPrintStream)](https://github.com/Marcono1234/HotJava-1.0-alpha/blob/master/hj-alpha3/hotjava/doc/misc/changes.html)