### Summary


Adds the Foreign Function & Memory API in [`java.lang.foreign`](https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/lang/foreign/package-summary.html).

### Details

The Foreign Function & Memory (FFM) API provides supported, safe access to:

- foreign (off-heap) memory via arenas and memory segments
- calling native functions without JNI via linkers and method handles

Key types include:

- [`Arena`](https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/lang/foreign/Arena.html) for scoped memory management
- [`MemorySegment`](https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/lang/foreign/MemorySegment.html) for representing regions of memory
- [`Linker`](https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/lang/foreign/Linker.html) and [`SymbolLookup`](https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/lang/foreign/SymbolLookup.html) for looking up and calling native functions

### Example

```java
// Call a native function using the FFM API (simplified example)
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

class Example {
    long demo() throws Throwable {
        Linker linker = Linker.nativeLinker();
        try (Arena arena = Arena.ofConfined()) {
            MethodHandle mh = linker.downcallHandle(
                    SymbolLookup.loaderLookup().find("strlen").orElseThrow(),
                    FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS)
            );
            // Real code would allocate a C string and pass its address; this is illustrative.
            return (long) mh.invokeExact(ValueLayout.ADDRESS); // placeholder call
        }
    }
}
```

### Historical

Finalized as a standard API in Java 22 (after multiple incubator/preview iterations) via JEP 454.

### Links

- [Package java.lang.foreign (Oracle Javadoc, Java 22)](https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/lang/foreign/package-summary.html)
- [Linker (Oracle Javadoc, Java 22)](https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/lang/foreign/Linker.html)
- [Arena (Oracle Javadoc, Java 22)](https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/lang/foreign/Arena.html)
- [JEP 454: Foreign Function & Memory API](https://openjdk.org/jeps/454)