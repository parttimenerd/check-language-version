// Java 8 edge case: Type annotation
// Test: Annotation in type use position (Java 8+)
// Expected Version: 8
// Required Features: TYPE_ANNOTATIONS, ANNOTATIONS, GENERICS, COLLECTIONS_FRAMEWORK

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@interface AnnotationType {}

@AnnotationType
class Edge_TypeAnnotation {
    java.util.List<String> s;
}