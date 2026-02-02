### Summary

Adds the [`java.lang.classfile`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/lang/classfile/package-summary.html) API for reading and writing Java class files.

### Details

The [`java.lang.classfile`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/lang/classfile/package-summary.html) package provides a standard, supported API to **parse**, **inspect**, and **generate** Java `.class` files.

Typical use cases include build tools, bytecode analyzers, and instrumentation utilities that need to work directly with the class file format. The API is designed as a supported alternative to ad-hoc or internal bytecode tooling.

Entry points include [`ClassFile`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/lang/classfile/ClassFile.html) (for reading/writing) and model types like [`ClassModel`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/lang/classfile/ClassModel.html).

In many cases you will:

- read class bytes from disk (e.g., via [`Files.readAllBytes(Path)`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/nio/file/Files.html#readAllBytes(java.nio.file.Path)))
- parse them into a model (`ClassModel`)
- inspect constant pool / members via the model APIs
- optionally write a modified class out again

### Example

```java
// Parse a .class file using java.lang.classfile
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.nio.file.Files;
import java.nio.file.Path;

class Example {
    void demo(Path classFilePath) throws Exception {
        byte[] bytes = Files.readAllBytes(classFilePath); // read class file bytes
        ClassModel model = ClassFile.of().parse(bytes);   // parse to a model
        String name = model.thisClass().asInternalName(); // internal name, e.g. java/lang/String
    }
}
```

### Historical

Introduced as a standard library API in Java 24.

### Links

- [Package java.lang.classfile (Oracle Javadoc, Java 24)](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/lang/classfile/package-summary.html)
- [ClassFile (Oracle Javadoc, Java 24)](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/lang/classfile/ClassFile.html)
- [ClassModel (Oracle Javadoc, Java 24)](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/lang/classfile/ClassModel.html)
- [Files.readAllBytes(Path) (Oracle Javadoc, Java 24)](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/nio/file/Files.html#readAllBytes(java.nio.file.Path))