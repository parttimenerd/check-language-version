// Tricky: Regex with assert
// Expected Version: 4
// Required Features: REGEX, ASSERT
import java.util.regex.Pattern;

public class Tiny_RegexAssert_Java4 {
    void match(String s) {
        assert Pattern.matches("\\d+", s);
    }
}