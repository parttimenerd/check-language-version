// Java 7 combination: Generics + Diamond + For-each
// Test: Combination of generics with diamond operator and for-each loops
// Expected Version: 7
// Required Features: GENERICS, DIAMOND_OPERATOR, FOR_EACH
import java.util.*;
public class Combo_GenericsDiamondForEach_Java7 {
    public void test() {
        List<String> list = new ArrayList<>();
        for (String s : list) {
            System.out.println(s);
        }
    }
}