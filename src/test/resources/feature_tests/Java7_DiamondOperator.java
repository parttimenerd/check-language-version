// Java 7 feature: Diamond operator
// Expected Version: 7
// Required Features: AUTOBOXING, COLLECTIONS_FRAMEWORK, DIAMOND_OPERATOR, GENERICS
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Java7_DiamondOperator {
    public void method() {
        // Diamond operator - type inference for generic constructors
        List<String> list = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();

        list.add("test");
        map.put("key", 42);
    }
}