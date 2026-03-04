// Java 5 edge case: Declaration annotation on a class
// Test: @Target(ElementType.TYPE) annotation applied to a class declaration (Java 5+)
// Note: This is NOT a type-use annotation (ElementType.TYPE_USE, Java 8).
// Expected Version: 5
// Required Features: ANNOTATIONS, GENERICS, COLLECTIONS_FRAMEWORK

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@interface AnnotationType {}

@AnnotationType
class Edge_TypeAnnotation {
    java.util.List<String> s;
}