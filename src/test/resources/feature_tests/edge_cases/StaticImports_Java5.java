// Edge case: Static imports
// Expected Version: 5
// Required Features: COLLECTIONS_FRAMEWORK, GENERICS, STATIC_IMPORT
import static java.lang.Math.*;
import static java.util.Collections.*;

import java.util.*;

public class StaticImports_Java5 {

    public void testStaticMathImports() {
        double result = sqrt(16) + pow(2, 3);
        double angle = sin(PI / 2);
    }

    public void testStaticCollectionsImports() {
        List<String> list = new ArrayList<String>();
        list.add("b");
        list.add("a");
        sort(list);
        reverse(list);
    }
}