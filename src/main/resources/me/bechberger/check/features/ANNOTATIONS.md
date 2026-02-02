### Summary

Adds Java annotations (`@Annotation`) for declaring structured metadata on program elements.

### Details

Annotations are a language feature that let you attach **typed metadata** to program elements like classes, methods, fields, parameters, and packages.

To define an annotation type, you use the keyword `@interface`. Annotation elements look like parameterless methods and can have default values:

- `@interface MyAnno { String value(); int count() default 0; }`

Annotations are commonly used by tools and frameworks (e.g., dependency injection, test frameworks, and code generators). At runtime, annotations can be discovered via reflection if they are retained with [`@Retention`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/Retention.html) and [`RetentionPolicy.RUNTIME`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/RetentionPolicy.html#RUNTIME).

The meta-annotations in `java.lang.annotation` control where and how an annotation can be used:

- [`@Target(...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/Target.html) restricts which element kinds can be annotated (e.g., [`ElementType.METHOD`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/ElementType.html#METHOD)).
- [`@Retention(...)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/Retention.html) controls whether the annotation is kept in source only, in the class file, or available at runtime.
- [`@Documented`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/Documented.html) marks the annotation for inclusion in Javadoc.
- [`@Inherited`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/Inherited.html) makes class annotations inheritable by subclasses (only for class-level annotations).

### Example

```java
// Define and use a runtime-retained annotation
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Audit {
    String value();
}

class Example {
    @Audit("user.create") // annotation instance with an element value
    void createUser() {
        // ...
    }
}
```

### Historical

Annotations were introduced in Java 5 (as part of JSR 175) and became a foundation for many later Java ecosystem conventions. Java 8 later added related features such as repeating annotations and type annotations.

### Links

- [JSR 175: A Metadata Facility for the Java Programming Language](https://jcp.org/en/jsr/detail?id=175)
- [Package java.lang.annotation (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/package-summary.html)
- [Java Language Specification (Java SE 21), §9.7 Annotations](https://docs.oracle.com/javase/specs/jls/se21/html/jls-9.html#jls-9.7)