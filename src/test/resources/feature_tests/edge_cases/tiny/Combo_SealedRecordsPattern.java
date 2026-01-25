// Java 21 combination: Sealed classes + Records + Pattern matching
// Test: Combination of sealed classes with records and pattern matching
// Expected Version: 21
// Required Features: SEALED_CLASSES, RECORDS, RECORD_PATTERNS, SWITCH_PATTERN_MATCHING
public class Combo_SealedRecordsPattern_Java21 {
    sealed interface Expr permits Const, Add {}
    record Const(int value) implements Expr {}
    record Add(Expr left, Expr right) implements Expr {}

    public int eval(Expr e) {
        return switch (e) {
            case Const(int v) -> v;
            case Add(Expr l, Expr r) -> eval(l) + eval(r);
        };
    }
}