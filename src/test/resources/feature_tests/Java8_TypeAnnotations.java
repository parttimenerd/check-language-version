// Java 8 feature: Type annotations (JEP 104)
// Expected Version: 8
// Required Features: TYPE_ANNOTATIONS
import java.lang.annotation.*;
import java.util.List;

// Define a type annotation
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@interface NonNull {}

@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@interface Valid {}

public class Java8_TypeAnnotations {
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

    // Type annotation in generics
    public void processList(List<@Valid String> items) {
        for (@NonNull String item : items) {
            System.out.println(item);
        }
    }

    // Type annotation on array type
    public @NonNull String @NonNull [] getArray() {
        return new String[0];
    }

    // Type annotation on exception
    public void riskyMethod() throws @NonNull RuntimeException {
        throw new RuntimeException("test");
    }
}