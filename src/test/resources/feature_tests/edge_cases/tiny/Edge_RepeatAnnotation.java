// Java 8 edge case: Repeatable annotations
// Test: Same annotation used twice
// Expected Version: 8
// Required Features: REPEATING_ANNOTATIONS
@java.lang.annotation.Repeatable(As.class) @interface Edge_RepeatAnnotation { int value(); }
@interface As { Edge_RepeatAnnotation[] value(); }
@Edge_RepeatAnnotation(1) @Edge_RepeatAnnotation(2)
public class Tiny_RepeatAnno_Java8 {}