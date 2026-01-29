// Test: Type annotations (Java 8)
// Expected Version: 8
// Required Features: ANNOTATIONS, GENERICS, TYPE_ANNOTATIONS, COLLECTIONS_FRAMEWORK
import java.lang.annotation.*;
import java.util.List;
class Tiny_TypeAnnotations_Java8 {
    @Target(ElementType.TYPE_USE)
    @interface NonNull {}

    @NonNull String s = "test";
    List<@NonNull String> list;
}