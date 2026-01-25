// Java 5 combination: Annotations + Generics + Varargs
// Test: Combination of annotations, generics, and varargs
// Expected Version: 5
// Required Features: ANNOTATIONS, GENERICS, VARARGS
public class Combo_AnnotationsGenericsVarargs_Java5 {
    @SafeVarargs
    public final <T> void process(T... items) {
        for (T item : items) {
            System.out.println(item);
        }
    }
}