// Java 21 edge case: Nested record patterns with guards
// Test: Testing nested record patterns with when guards in switch
// Expected Version: 21
// Required Features: RECORD_PATTERNS, SWITCH_PATTERN_MATCHING
public class Edge_NestedRecordPatternsGuards_Java21 {
    record Outer(Inner inner) {}
    record Inner(String value) {}

    void test(Object obj) {
        switch (obj) {
            case Outer(Inner(String s)) when s.length() > 5 -> System.out.println("long");
            case Outer(Inner(String s)) -> System.out.println("short");
            default -> System.out.println("other");
        }
    }
}