// Java 5 combination: Varargs + Generics + Autoboxing
// Test: Combination of varargs with generics and autoboxing
// Expected Version: 5
// Required Features: AUTOBOXING, COLLECTIONS_FRAMEWORK, GENERICS, VARARGS
import java.util.*;
public class Combo_VarargsGenericsAutoboxing_Java5 {
    <T> List<T> asList(T... elements) {
        return Arrays.asList(elements);
    }
    void test() {
        List<Integer> nums = asList(1, 2, 3);
    }
}