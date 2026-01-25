// Test: Regex API (Java 4)
// Expected Version: 4
// Required Features: REGEX
import java.util.regex.*;
public class Tiny_Regex_Java4 {
    public void test() {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher("123");
        boolean found = m.find();
    }
}