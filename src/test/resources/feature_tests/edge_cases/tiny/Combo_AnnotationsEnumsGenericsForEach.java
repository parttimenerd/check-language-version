// Java 5 combination: Annotations + Enums + Generics + For-each
// Test: Combination of annotations, enums, generics, and for-each loops
// Expected Version: 5
// Required Features: ANNOTATIONS, ENUMS, GENERICS, FOR_EACH
import java.util.*;

public class Combo_AnnotationsEnumsGenericsForEach_Java5 {
    enum Status { ACTIVE, INACTIVE }

    @SuppressWarnings("unchecked")
    public void process() {
        List<Status> statuses = new ArrayList<Status>();
        for (Status s : Status.values()) {
            System.out.println(s);
        }
    }
}