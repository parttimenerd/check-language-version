// Test: Generics (Java 5)
// Expected Version: 5
// Required Features: GENERICS
import java.util.List;
import java.util.ArrayList;
public class Tiny_Generics_Java5 {
    public <T> List<T> createList() {
        return new ArrayList<T>();
    }
}