### Summary

Adds `StringInputStream` for treating a `String` as an input stream.

### Details

HotJava/Java 1.0-alpha3 added `java.io.StringInputStream`, an early convenience class that allowed reading stream data from a `String`.

In modern Java, similar behavior is usually done via `ByteArrayInputStream` (after encoding) or via readers like `StringReader`.

### Links

- [HotJava 1.0 alpha3 changes (added java.io classes including StringInputStream)](https://github.com/Marcono1234/HotJava-1.0-alpha/blob/master/hj-alpha3/hotjava/doc/misc/changes.html)