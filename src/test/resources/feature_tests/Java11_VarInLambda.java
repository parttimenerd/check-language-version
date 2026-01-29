// Java 11 feature: Var in lambda parameters
// Expected Version: 11
// Required Features: GENERICS, LAMBDAS, VAR_IN_LAMBDA
import java.util.function.*;
class Java11_VarInLambda {
    public void method() {
        BiFunction<String, String, String> concat = (var a, var b) -> a + b;
        Consumer<String> printer = (var s) -> System.out.println(s);
    }
}