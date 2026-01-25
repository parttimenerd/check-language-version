// Java 8 combination: Repeating annotations + Type annotations
// Test: Combination of repeating annotations with type annotations
// Expected Version: 8
// Required Features: REPEATING_ANNOTATIONS
// Note: TYPE_ANNOTATIONS detection depends on how JavaParser represents annotations on types
import java.lang.annotation.*;
public class Combo_RepeatingTypeAnnotations_Java8 {
    @Repeatable(Tags.class)
    @Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
    @interface Tag { String value(); }

    @interface Tags { Tag[] value(); }

    @Tag("a") @Tag("b")
    String s = "test";
}