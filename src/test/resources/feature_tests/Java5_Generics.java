// Java 5 feature: Generics
// Expected Version: 5
// Required Features: GENERICS
import java.util.List;
import java.util.ArrayList;

public class Java5_Generics {
    public <T> T genericMethod(T param) {
        return param;
    }

    public void useGenerics() {
        List<String> list = new ArrayList<String>();
        list.add("test");
    }
}