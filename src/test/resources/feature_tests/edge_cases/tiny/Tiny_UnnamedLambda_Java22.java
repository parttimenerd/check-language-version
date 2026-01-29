// Tiny: Unnamed lambda param (Java 22)
// Expected Version: 22
// Required Features: GENERICS, LAMBDAS, UNNAMED_VARIABLES

class Tiny_UnnamedLambda_Java22 {
    java.util.function.BiConsumer<String,String> c = (_, _) -> {};
}