// Java 5 combination: For-each + Enums + Static import
// Test: Combination of for-each loops with enums and static imports
// Expected Version: 5
// Required Features: FOR_EACH, ENUMS, STATIC_IMPORT
import static java.lang.System.out;
class Combo_ForEachEnumStatic_Java5 {
    enum Color { RED, GREEN, BLUE }
    void test() {
        for (Color c : Color.values()) {
            out.println(c);
        }
    }
}