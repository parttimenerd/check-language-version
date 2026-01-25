// Java 10 combination: Var + Diamond + Collection factory
// Test: Combination of var with diamond operator and collection factory methods
// Expected Version: 10
// Required Features: VAR, DIAMOND_OPERATOR, COLLECTION_FACTORY_METHODS
import java.util.*;
public class Combo_VarDiamondFactory_Java10 {
    public void test() {
        // Diamond operator requires explicit type on left side
        List<String> list = new ArrayList<>();
        var immutable = List.of("a", "b", "c");
        var x = "test"; // var usage
    }
}