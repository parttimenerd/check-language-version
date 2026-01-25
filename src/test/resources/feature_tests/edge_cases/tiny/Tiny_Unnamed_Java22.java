// Test: Unnamed variables (Java 22)
// Expected Version: 22
// Required Features: COLLECTION_FACTORY_METHODS, FOR_EACH, UNNAMED_VARIABLES
import java.util.*;
public class Tiny_Unnamed_Java22 {
    public void test() {
        for (String _ : List.of("a", "b")) {}
    }
}