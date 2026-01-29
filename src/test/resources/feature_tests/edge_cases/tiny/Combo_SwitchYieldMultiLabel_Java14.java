// Java 14 combination: Switch expressions + Yield + Multiple labels
// Test: Combination of switch expressions with yield and multiple case labels
// Expected Version: 14
// Required Features: SWITCH_EXPRESSIONS, YIELD, SWITCH_MULTIPLE_LABELS
class Combo_SwitchYieldMultiLabel_Java14 {
    int test(int x) {
        return switch (x) {
            case 1, 2, 3:
                yield 10;
            case 4, 5:
                yield 20;
            default:
                yield 0;
        };
    }
}