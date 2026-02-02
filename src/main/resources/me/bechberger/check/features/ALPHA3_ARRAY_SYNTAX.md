### Summary

Adds the modern array declaration syntax using `T[]`.

### Details

HotJava/Java 1.0-alpha3 introduced a new array declaration syntax where the `[]` belongs to the type:

- `int[] i;`
- `int[] method();`

This is now the standard Java syntax. It makes it clearer when declaring arrays as return types.

### Example

```java
// New alpha3 array declaration syntax
class Example {
    int[] values;

    int[] method() {
        // return an array
        return new int[] { 1, 2, 3 }; // modern style
    }
}
```

### Historical

Added in HotJava/Java 1.0-alpha3 as a language syntax change.

### Links

- [HotJava 1.0 alpha3 changes (language changes: array declaration syntax)](https://github.com/Marcono1234/HotJava-1.0-alpha/blob/master/hj-alpha3/hotjava/doc/misc/changes.html)
- [Java arrays (current tutorial, for background)](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/arrays.html)