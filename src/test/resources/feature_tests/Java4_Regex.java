// Java 4 feature: Regular expressions (java.util.regex)
// Expected Version: 4
// Required Features: ALPHA3_ARRAY_SYNTAX, REGEX
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Java4_Regex {
    public void testRegex() {
        // Compile a pattern
        Pattern pattern = Pattern.compile("\\d+");

        // Match against input
        Matcher matcher = pattern.matcher("abc123def456ghi789");

        // Find all matches
        while (matcher.find()) {
            System.out.println("Found: " + matcher.group() + " at index " + matcher.start());
        }

        // Test if entire string matches
        Pattern emailPattern = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$", Pattern.CASE_INSENSITIVE);
        boolean isValidEmail = emailPattern.matcher("test@example.com").matches();
        System.out.println("Valid email: " + isValidEmail);

        // Replace all
        String result = pattern.matcher("a1b2c3").replaceAll("X");
        System.out.println("Replaced: " + result);  // aXbXcX

        // Split
        Pattern comma = Pattern.compile(",\\s*");
        String[] parts = comma.split("a, b, c");
        for (int i = 0; i < parts.length; i++) {
            System.out.println("Part: " + parts[i]);
        }

        // Groups
        Pattern groupPattern = Pattern.compile("(\\w+)=(\\d+)");
        Matcher groupMatcher = groupPattern.matcher("key=123");
        if (groupMatcher.matches()) {
            System.out.println("Key: " + groupMatcher.group(1));
            System.out.println("Value: " + groupMatcher.group(2));
        }
    }
}