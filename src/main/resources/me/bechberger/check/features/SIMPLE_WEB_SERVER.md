### Summary


Adds the `jwebserver` tool, a simple static-file web server.

### Details

Java 18 introduced `jwebserver`, a command-line tool that starts a minimal HTTP server for serving static files from a directory.

It is intended for:

- local development
- demos
- quick sharing of static content

It is not designed as a full-featured production server.

### Example

```java
// jwebserver is a command-line tool; start it from a directory to serve files
// Example shell usage: jwebserver --port 8000
class Example {}
```

### Historical

Introduced in Java 18 via JEP 408.

### Links

- [JEP 408: Simple Web Server](https://openjdk.org/jeps/408)
- [jwebserver tool documentation (Oracle)](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jwebserver.html)