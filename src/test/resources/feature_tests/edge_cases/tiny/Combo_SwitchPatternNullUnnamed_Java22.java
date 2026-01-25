// Java 22 combination: Switch pattern + Null default + Unnamed variables
// Test: Combination of switch pattern matching with null/default and unnamed variables
// Expected Version: 22
// Required Features: SWITCH_PATTERN_MATCHING, SWITCH_NULL_DEFAULT, UNNAMED_VARIABLES
public class Combo_SwitchPatternNullUnnamed_Java22 {
    void test(Object obj) {
        switch (obj) {
            case String s -> System.out.println(s);
            case Integer _ -> System.out.println("int");
            case null, default -> System.out.println("other");
        }
    }
}