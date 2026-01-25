// Edge case: Annotation variations
// Expected Version: 8
// Required Features: ANNOTATIONS, REFLECTION, REPEATING_ANNOTATIONS
// Note: TYPE_ANNOTATIONS detection is complex as it depends on how JavaParser parses annotations on types
import java.lang.annotation.*;
import java.util.*;

public class AnnotationEdgeCases_Java8 {

    // Type annotation for TYPE_ANNOTATIONS detection
    @Target(ElementType.TYPE_USE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface NonNull {}

    // Type annotation on field type
    private @NonNull String name;

    // Type annotation on method return type
    public @NonNull String getName() {
        return name;
    }

    // Type annotation on parameter type
    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Deprecated
    public void deprecatedMethod() {}

    @SuppressWarnings("unchecked")
    public void suppressedMethod() {}

    @Override
    public String toString() {
        return "AnnotationEdgeCases";
    }

    // Repeating annotations
    @Schedule(day = "Monday")
    @Schedule(day = "Wednesday")
    public void repeatingAnnotations() {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Repeatable(Schedules.class)
    @interface Schedule {
        String day();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Schedules {
        Schedule[] value();
    }
}