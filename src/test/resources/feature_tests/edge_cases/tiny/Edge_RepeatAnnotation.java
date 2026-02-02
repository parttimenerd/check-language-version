// Java 8 edge case: Repeatable annotations
// Test: Same annotation used twice
// Expected Version: 8
// Required Features: ANNOTATIONS, REPEATING_ANNOTATIONS, TYPE_ANNOTATIONS, CLASS_PROPERTY
import java.lang.annotation.Repeatable;

@Repeatable(As.class)
@interface Edge_RepeatAnnotation {
    int value();
}
@interface As { Edge_RepeatAnnotation[] value(); }
@Edge_RepeatAnnotation(1)
@Edge_RepeatAnnotation(2)
class Tiny_RepeatAnno_Java8 {}