// Tiny: Annotation on type use (Java 8)
// Expected Version: 8
// Required Features: ANNOTATIONS, COLLECTIONS_FRAMEWORK, GENERICS, TYPE_ANNOTATIONS

import java.util.*;
import java.lang.annotation.*;

public class Tiny_TypeAnno_Java8 {
    @Target(ElementType.TYPE_USE)
    @interface NonNull {}

    List<@NonNull String> s;
}