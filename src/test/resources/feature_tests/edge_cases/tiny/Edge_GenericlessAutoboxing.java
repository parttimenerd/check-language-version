// Java 5 edge case: Autoboxing without obvious generics
// Test: Subtle autoboxing that looks like it could be Java 1
// Expected Version: 5
// Required Features: AUTOBOXING
public class Edge_GenericlessAutoboxing {
    Integer boxed = 42;
    int unboxed = boxed;
}