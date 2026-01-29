// Edge case: Copy of Java5_Generics + unqualified IO usage in a compact source file.
// Expected Version: 25
// Required Features: COLLECTIONS_FRAMEWORK, GENERICS, IMPLICITLY_IMPORTED_IO_CLASS, IO_CLASS

import java.util.List;
import java.util.ArrayList;

class IO_OverridesVersion_Generics_Java25 {
    public <T> T genericMethod(T param) {
        return param;
    }

    public void useGenerics() {
        List<String> list = new ArrayList<String>();
        list.add(IO.readln());
    }
}