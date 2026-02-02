### Summary


Adds the Scripting API in [`javax.script`](https://docs.oracle.com/en/java/javase/21/docs/api/java.scripting/javax/script/package-summary.html) (JSR 223).

### Details

The Scripting API allows Java applications to embed and interact with scripting languages through a common interface.

Key types include:

- [`ScriptEngineManager`](https://docs.oracle.com/en/java/javase/21/docs/api/java.scripting/javax/script/ScriptEngineManager.html) to locate script engines
- [`ScriptEngine`](https://docs.oracle.com/en/java/javase/21/docs/api/java.scripting/javax/script/ScriptEngine.html) to evaluate scripts
- [`Bindings`](https://docs.oracle.com/en/java/javase/21/docs/api/java.scripting/javax/script/Bindings.html) to pass variables between Java and the script

Actual language availability depends on which engines are present (for example, JavaScript engines were historically bundled, but that has changed over time).

### Example

```java
// Evaluate a simple script (engine availability depends on the runtime)
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

class Example {
    Object demo() throws Exception {
        ScriptEngine e = new ScriptEngineManager().getEngineByName("javascript");
        if (e == null) return null; // no engine installed
        return e.eval("1 + 2"); // evaluate expression
    }
}
```

### Historical

Introduced in Java 6 via JSR 223 to standardize script embedding.

### Links

- [Package javax.script (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.scripting/javax/script/package-summary.html)
- [ScriptEngineManager (Oracle Javadoc)](https://docs.oracle.com/en/java/javase/21/docs/api/java.scripting/javax/script/ScriptEngineManager.html)
- [JSR 223: Scripting for the Java Platform](https://jcp.org/en/jsr/detail?id=223)