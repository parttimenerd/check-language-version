### Summary


Allows using `var` for lambda parameters.

### Details

Java 11 allows lambda parameters to be declared with `var`, enabling:

- consistent style when you want to use `var` in the parameter list
- attaching annotations to inferred parameter types

Rule of thumb: if you use `var` for one parameter in a lambda, you must use it for all parameters in that lambda.

### Example

```java
// Use var in lambda parameters
import java.util.function.BiFunction;

class Example {
    BiFunction<String, String, String> demo() {
        return (var a, var b) -> a + b; // var for both params
    }
}
```

### Historical

Introduced in Java 11 via JEP 323.

### Links

- [JEP 323: Local-Variable Syntax for Lambda Parameters](https://openjdk.org/jeps/323)
- [JLS §15.27 Lambda Expressions (Java SE 21)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.27)