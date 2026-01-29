// Java 21 edge case: null case in switch
// Test: Switch with null handling
// Expected Version: 21
// Required Features: SWITCH_NULL_DEFAULT, STRINGS_IN_SWITCH, SWITCH_EXPRESSIONS
class Edge_SwitchNull {
    void test(String s) {
        switch (s) {
            case null -> System.out.println("null");
            case "a" -> System.out.println("a");
            default -> System.out.println("other");
        }
    }
}