// Java 22 combination: Unnamed variables + Records + Pattern matching + Switch
// Test: Combination of unnamed variables with records, pattern matching, and switch
// Expected Version: 22
// Required Features: RECORDS, RECORD_PATTERNS, SWITCH_EXPRESSIONS, SWITCH_PATTERN_MATCHING, UNNAMED_VARIABLES
class Combo_UnnamedRecordsPatternSwitch {
    record Point(int x, int y) {}
    record Point3D(int x, int y, int z) {}

    void test(Object obj) {
        switch (obj) {
            case Point(int x, int _) -> System.out.println("x=" + x);
            case Point3D(int x, int _, int _) -> System.out.println("x=" + x);
            case String _ -> System.out.println("string");
            default -> {}
        }
    }
}