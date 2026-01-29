// Java 5 combination: Annotations + Enums + Generics + For-each
// Test: Combination of annotations, enums, generics, and for-each loops
// Expected Version: 5
// Required Features: ANNOTATIONS, COLLECTIONS_FRAMEWORK, ENUMS, FOR_EACH, GENERICS
import java.util.*;

class Combo_AnnotationsEnumsGenericsForEach {
    enum Status { ACTIVE, INACTIVE }

    @SuppressWarnings("unchecked")
    public void process() {
        List<Status> statuses = new ArrayList<Status>();
        for (Status s : Status.values()) {
            System.out.println(s);
        }
    }
}