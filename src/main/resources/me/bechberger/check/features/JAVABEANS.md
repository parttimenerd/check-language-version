### Summary


Adds the JavaBeans component model and introspection APIs in [`java.beans`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/java/beans/package-summary.html).

### Details

JavaBeans is a set of conventions and supporting APIs for reusable components.

A JavaBean typically:

- is a public class with a public no-arg constructor
- exposes properties via getters/setters like `getX()` / `setX(...)` (or boolean `isX()`)

The [`java.beans`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/java/beans/package-summary.html) package provides introspection support so tools can discover properties and events.

Key types include:

- [`Introspector`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/java/beans/Introspector.html) for obtaining bean info
- [`PropertyDescriptor`](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/java/beans/PropertyDescriptor.html) to describe a property

### Example

```java
// A simple JavaBean with a property
class Person {
    private String name;

    public Person() {} // required by many JavaBeans tools

    public String getName() {
        return name; // getter defines the "name" property
    }

    public void setName(String name) {
        this.name = name; // setter updates the property
    }
}
```

### Historical

Introduced in Java 1.1 and heavily used by GUI builders and frameworks that rely on reflection and naming conventions.

### Links

- [Package java.beans (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/java/beans/package-summary.html)
- [Introspector (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/java/beans/Introspector.html)
- [PropertyDescriptor (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/java/beans/PropertyDescriptor.html)