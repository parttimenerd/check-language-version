// Edge case: Autoboxing variations
// Expected Version: 5
// Required Features: AUTOBOXING, COLLECTIONS_FRAMEWORK, GENERICS
import java.util.*;

class AutoboxingEdgeCases_Java5 {

    private Integer fieldInt = 42;
    private Double fieldDouble = 3.14;

    public void testAutoboxing() {
        Integer a = 1;
        Double b = 2.5;
        Long c = 100L;
        Boolean d = true;

        List<Integer> numbers = new ArrayList<Integer>();
        numbers.add(1);
        numbers.add(2);

        int sum = a + 10;
    }
}