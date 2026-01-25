// Java 10 combination: Generics + Var + Diamond
// Test: Combination of generics with var and diamond operator
// Expected Version: 10
// Required Features: COLLECTIONS_FRAMEWORK, DIAMOND_OPERATOR, GENERICS, VAR
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Combo_GenericsVarDiamond_Java10 {
    void test() {
        // Use explicit generic type on left side to allow diamond on right
        List<String> list = new ArrayList<>();
        Map<Integer, String> map = new HashMap<>();
        // Also use var with explicit generics
        var explicitList = new ArrayList<String>();
    }
}