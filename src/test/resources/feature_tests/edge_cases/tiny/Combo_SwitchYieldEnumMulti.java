// Java 14 combination: Switch expressions + Yield + Enums + Multiple labels
// Test: Combination of switch expressions with yield, enums, and multiple case labels
// Expected Version: 14
// Required Features: SWITCH_EXPRESSIONS, YIELD, ENUMS, SWITCH_MULTIPLE_LABELS
class Combo_SwitchYieldEnumMulti {
    enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

    int getDayType(Day day) {
        return switch (day) {
            case MON, TUE, WED, THU, FRI:
                yield 1; // weekday
            case SAT, SUN:
                yield 2; // weekend
        };
    }
}