// Java 16 feature: Local enums (defined inside methods)
// Expected Version: 16
// Required Features: ENUMS, LOCAL_ENUMS, SWITCH_EXPRESSIONS

class Java16_LocalEnums {

    // Java 16 allows enums, interfaces, and records inside methods
    // This is part of JEP 395 (Records) which allows local declarations

    public String processStatus() {
        // Local enum defined inside a method (Java 16+)
        enum LocalStatus {
            PENDING, APPROVED, REJECTED
        }

        LocalStatus status = LocalStatus.PENDING;
        return switch (status) {
            case PENDING -> "Waiting...";
            case APPROVED -> "Approved!";
            case REJECTED -> "Rejected.";
        };
    }

    // Static nested enum (works in all Java versions with enums)
    enum Status {
        PENDING, APPROVED, REJECTED
    }

    public void demonstrateLocalEnumConcept() {
        // Another local enum
        enum AnotherLocalEnum { A, B, C }

        AnotherLocalEnum e = AnotherLocalEnum.A;

        switch (e) {
            case A -> System.out.println("A!");
            case B -> System.out.println("B!");
            case C -> System.out.println("C!");
        }
    }
}