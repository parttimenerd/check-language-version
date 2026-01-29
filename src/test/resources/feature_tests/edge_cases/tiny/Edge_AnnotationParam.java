// Java 5 edge case: Annotation on parameter
// Test: Annotation in subtle position
// Expected Version: 5
// Required Features: ANNOTATIONS
class Edge_AnnotationParam {
    void test(@Deprecated String s) {}
}