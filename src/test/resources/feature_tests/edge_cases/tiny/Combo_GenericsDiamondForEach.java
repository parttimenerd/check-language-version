// Java 7 combination: Generics + Diamond + For-each
// Test: Combination of generics with diamond operator and for-each loops
// Expected Version: 7
// Required Features: COLLECTIONS_FRAMEWORK, DIAMOND_OPERATOR, FOR_EACH, GENERICS
import java.util.*;
class Combo_GenericsDiamondForEach {
    public void test() {
        List<String> list = new ArrayList<>();
        for (String s : list) {
            System.out.println(s);
        }
    }
}