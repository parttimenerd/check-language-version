// Tiny: Var in lambda annotated (Java 11)
// Expected Version: 11
// Required Features: ANNOTATIONS, GENERICS, LAMBDAS, TYPE_ANNOTATIONS, VAR_IN_LAMBDA

public class Tiny_VarLambdaAnno_Java11 {
    java.util.function.Function<String,String> f = (@Deprecated var s) -> s;
}