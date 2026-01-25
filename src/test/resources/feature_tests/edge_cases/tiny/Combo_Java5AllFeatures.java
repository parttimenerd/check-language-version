// Java 5 combination: All major features
// Test: Combination of generics, enums, annotations, varargs, for-each, static imports, and autoboxing
// Expected Version: 5
// Required Features: ANNOTATIONS, AUTOBOXING, COLLECTIONS_FRAMEWORK, ENUMS, FOR_EACH, GENERICS, VARARGS
import java.util.*;
public class Combo_Java5AllFeatures {
    enum Priority { LOW, MEDIUM, HIGH }

    @SuppressWarnings("unchecked")
    public <T> void process(T... items) {
        List<T> list = new ArrayList<T>();
        for (T item : items) {
            list.add(item);
        }

        Priority p = Priority.HIGH;
        Integer boxed = 42; // autoboxing
    }
}