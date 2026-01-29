// Java 5 combination: Enums + Annotations + Generics
// Test: Combination of enums with annotations and generics
// Expected Version: 5
// Required Features: ANNOTATIONS, COLLECTIONS_FRAMEWORK, ENUMS, GENERICS
import java.util.List;
class Combo_EnumAnnotationGenerics_Java5 {
    @Deprecated
    enum Status {
        ACTIVE, INACTIVE
    }
    List<Status> statuses;
}