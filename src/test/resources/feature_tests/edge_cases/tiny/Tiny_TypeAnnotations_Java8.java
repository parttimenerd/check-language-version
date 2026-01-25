// Test: Type annotations (Java 8)
// Expected Version: 8
// Required Features: TYPE_ANNOTATIONS
import java.lang.annotation.*;
public class Tiny_TypeAnnotations_Java8 {
    @Target(ElementType.TYPE_USE)
    @interface NonNull {}

    @NonNull String s = "test";
    List<@NonNull String> list;
}

interface List<T> {}