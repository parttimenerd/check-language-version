// Java 5 feature: Annotations
// Expected Version: 5
// Required Features: ANNOTATIONS
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@interface MyAnnotation {
    String value() default "";
}

public class Java5_Annotations {
    @Override
    public String toString() {
        return "test";
    }

    @Deprecated
    public void oldMethod() {}

    @MyAnnotation("test")
    public void annotatedMethod() {}
}