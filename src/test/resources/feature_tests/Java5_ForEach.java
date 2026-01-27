// Java 5 feature: Enhanced for loop (for-each)
// Expected Version: 5
// Required Features: ALPHA3_ARRAY_SYNTAX, COLLECTIONS_FRAMEWORK, FOR_EACH, GENERICS
import java.util.List;
import java.util.Arrays;

public class Java5_ForEach {
    public void iterate() {
        List<String> list = Arrays.asList("a", "b", "c");
        for (String item : list) {
            System.out.println(item);
        }

        int[] numbers = {1, 2, 3, 4, 5};
        for (int n : numbers) {
            System.out.println(n);
        }
    }
}