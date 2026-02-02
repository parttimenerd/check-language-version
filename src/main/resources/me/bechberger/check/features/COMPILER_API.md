### Summary

Adds the Java Compiler API in [`javax.tools`](https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/tools/package-summary.html).

### Details

The [`javax.tools`](https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/tools/package-summary.html) API provides a standard way to invoke the Java compiler from within Java code.

Common entry points include:

- [`ToolProvider.getSystemJavaCompiler()`](https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/tools/ToolProvider.html#getSystemJavaCompiler()) to obtain a [`JavaCompiler`](https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/tools/JavaCompiler.html)
- [`JavaCompiler.getTask(...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/tools/JavaCompiler.html#getTask(java.io.Writer,javax.tools.JavaFileManager,javax.tools.DiagnosticListener,java.lang.Iterable,java.lang.Iterable,java.lang.Iterable)) to configure and run a compilation task
- [`StandardJavaFileManager`](https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/tools/StandardJavaFileManager.html) for working with files on disk

This is often used by build tools, IDEs, annotation processors, and code-generation pipelines.

Note: `getSystemJavaCompiler()` returns `null` on a JRE-only runtime without the compiler (historically relevant; with modern JDK distributions, it is typically present).

### Example

```java
// Compile a .java file using the Java Compiler API
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

class Example {
    int demo(String pathToJavaFile) {
        JavaCompiler c = ToolProvider.getSystemJavaCompiler(); // may be null on a JRE-only runtime
        if (c == null) return -1;
        return c.run(null, null, null, List.of(pathToJavaFile).toArray(new String[0]));
    }
}
```

### Historical

Introduced in Java 6 as part of JSR 199 (standardized tools and compiler APIs).

### Links

- [Package javax.tools (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/tools/package-summary.html)
- [JSR 199: Java Compiler API](https://jcp.org/en/jsr/detail?id=199)
- [ToolProvider.getSystemJavaCompiler() (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/tools/ToolProvider.html#getSystemJavaCompiler())