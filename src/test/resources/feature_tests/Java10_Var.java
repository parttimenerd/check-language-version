// Java 10 feature: Local variable type inference (var)
// Expected Version: 10
// Required Features: VAR
import java.util.*;
public class Java10_Var {
    public void method() {
        var list = new ArrayList<String>();
        var map = new HashMap<String, Integer>();
        var number = 42;
        var text = "Hello";
        for (var item : list) {
            System.out.println(item);
        }
    }
}
