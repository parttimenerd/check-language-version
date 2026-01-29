// Edge case: Copy of Java7_DiamondOperator + unqualified IO usage in a compact source file.
// Expected Version: 25
// Required Features: AUTOBOXING, COLLECTIONS_FRAMEWORK, COMPACT_SOURCE_FILES, DIAMOND_OPERATOR, GENERICS, IMPLICITLY_IMPORTED_IO_CLASS, IO_CLASS

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

void main() {
    List<String> list = new ArrayList<>();
    Map<String, Integer> map = new HashMap<>();

    list.add("test");
    map.put("key", 42);
    IO.println("List: " + list + ", Map: " + map);
}