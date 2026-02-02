// Java 8 combination: Repeating annotations + Type annotations
// Test: Combination of repeating annotations with type annotations
// Expected Version: 8
// Required Features: ANNOTATIONS, REPEATING_ANNOTATIONS, TYPE_ANNOTATIONS
// Note: TYPE_ANNOTATIONS detection depends on how JavaParser represents annotations on types
import java.lang.annotation.*;
class Combo_RepeatingTypeAnnotations_Java8 {
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Tags { Tag[] value(); }

    @Repeatable(Tags.class)
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Tag { String value(); }


    @Tag("a") @Tag("b")
    String s = "test";
}