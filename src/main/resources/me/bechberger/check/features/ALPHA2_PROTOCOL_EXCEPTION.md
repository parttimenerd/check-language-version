### Summary

Adds `ProtocolException` for signaling network protocol errors.

### Details

HotJava/Java 1.0-alpha2 shipped early networking support and introduced `ProtocolException` as a dedicated exception type for reporting protocol-level errors.

In modern Java this is represented by `java.net.ProtocolException` and is typically used by protocol implementations to indicate invalid or unexpected protocol data.

- [HotJava 1.0 alpha2 changes (added ProtocolException)](https://github.com/Marcono1234/HotJava-1.0-alpha/blob/master/hj-alpha2/hotjava/doc/changes/changes.html)