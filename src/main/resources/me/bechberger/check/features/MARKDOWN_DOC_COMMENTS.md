### Summary


Allows using Markdown syntax in Javadoc documentation comments.

### Details

This feature allows documentation comments processed by the Javadoc tool to be written in Markdown, making it easier to author readable documentation with lists, emphasis, code, and links.

The intent is to make Java documentation more approachable and consistent with widely used documentation formats.

### Example

```java
/// Javadoc comment with Markdown
/// ### Example
/// Here is a list:
/// This is a list item.
/// Use `inline code`.
class Example {}
```

### Historical

Introduced in Java 23 via JEP 467 to support Markdown-based documentation in the standard toolchain.

### Links

- [JEP 467: Markdown Documentation Comments](https://openjdk.org/jeps/467)
- [javadoc tool documentation (Oracle)](https://docs.oracle.com/en/java/javase/21/docs/specs/man/javadoc.html)