// Java 5 combination: Enum + Switch + Annotations
// Test: Combination of enums in switch statements with annotations
// Expected Version: 5
// Required Features: ENUMS, ANNOTATIONS
public class Combo_EnumSwitchAnnotations {
    enum Status { ACTIVE, INACTIVE, PENDING }

    @SuppressWarnings("unused")
    public String getLabel(Status s) {
        switch (s) {
            case ACTIVE: return "Active";
            case INACTIVE: return "Inactive";
            case PENDING: return "Pending";
            default: return "Unknown";
        }
    }
}