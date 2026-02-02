### Summary


Adds user/system preference storage via [`java.util.prefs`](https://docs.oracle.com/en/java/javase/21/docs/api/java.prefs/java/util/prefs/package-summary.html).

### Details

The Preferences API provides a platform-independent way to store and retrieve small configuration values for a user or the system.

Key types include:

- [`Preferences`](https://docs.oracle.com/en/java/javase/21/docs/api/java.prefs/java/util/prefs/Preferences.html) as the main entry point
- accessing nodes via [`Preferences.userNodeForPackage(Class)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.prefs/java/util/prefs/Preferences.html#userNodeForPackage(java.lang.Class))

Values are stored as strings and primitives; the backing store depends on the OS (for example, the registry on Windows).

### Example

```java
// Store and read a preference
import java.util.prefs.Preferences;

class Example {
    void demo() {
        Preferences p = Preferences.userNodeForPackage(Example.class);
        p.put("theme", "dark"); // store a value
        String theme = p.get("theme", "light"); // read with default
    }
}
```

### Historical

Introduced in Java 1.4 as part of the standard library to provide a cross-platform preferences mechanism.

### Links

- [Package java.util.prefs (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.prefs/java/util/prefs/package-summary.html)
- [Preferences (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.prefs/java/util/prefs/Preferences.html)
- [Preferences.userNodeForPackage(Class) (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.prefs/java/util/prefs/Preferences.html#userNodeForPackage(java.lang.Class))