// Edge case: Annotation variations
// Expected Version: 8
// Required Features: TYPE_ANNOTATIONS, ANNOTATIONS, REPEATING_ANNOTATIONS
import java.lang.annotation.*;
import java.util.*;

class AnnotationEdgeCases_Java8 {

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