### Summary


Adds repeatable annotations via [`@Repeatable`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/Repeatable.html).

### Details

Repeatable annotations allow applying the same annotation type multiple times to a declaration.

A repeatable annotation is declared by:

- defining a *container* annotation that has a `value()` array
- marking the repeatable annotation with [`@Repeatable`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/Repeatable.html), referencing the container type

At runtime, reflection returns the container annotation, and utilities like [`AnnotatedElement.getAnnotationsByType(Class)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/AnnotatedElement.html#getAnnotationsByType(java.lang.Class)) can return the repeated annotations.

### Example

```java
// Define and use a repeatable annotation
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Tags.class)
@interface Tag {
    String value();
}

@Retention(RetentionPolicy.RUNTIME)
@interface Tags {
    Tag[] value(); // container
}

@Tag("a")
@Tag("b")
class Example {}
```

### Historical

Introduced in Java 8 as part of language/tooling updates that included type annotations and other annotation improvements.

### Links

- [Repeatable (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/annotation/Repeatable.html)
- [AnnotatedElement.getAnnotationsByType(Class) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/reflect/AnnotatedElement.html#getAnnotationsByType(java.lang.Class))
- [JLS §9.6 Annotations (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-9.html#jls-9.6)