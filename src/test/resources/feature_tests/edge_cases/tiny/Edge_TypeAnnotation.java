// Java 8 edge case: Type annotation
// Test: Annotation in type use position (Java 8+)
// Expected Version: 8
// Required Features: TYPE_ANNOTATIONS
public class Edge_TypeAnnotation {
    java.util.List<@Deprecated String> s;
}