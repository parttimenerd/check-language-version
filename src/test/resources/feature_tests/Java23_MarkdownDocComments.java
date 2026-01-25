// Java 23 feature: Markdown Documentation Comments (JEP 467)
// Expected Version: 23
// Required Features: MARKDOWN_DOC_COMMENTS
// Compile Check: false
// This file demonstrates the new Markdown syntax in Javadoc comments

/// This is a class that demonstrates **Markdown** in documentation.
///
/// ## Features
/// - Uses `///` for documentation
/// - Supports **bold** and *italic*
/// - Supports `inline code`
///
/// ### Example Usage
/// ```java
/// var demo = new Java23_MarkdownDocComments();
/// demo.greet("World");
/// ```
///
/// @see java.lang.String
public class Java23_MarkdownDocComments {

    /// Greets the specified person.
    ///
    /// This method prints a greeting message to the console.
    /// The greeting format is: `Hello, {name}!`
    ///
    /// @param name the name of the person to greet
    /// @return the greeting message
    public String greet(String name) {
        String message = "Hello, " + name + "!";
        System.out.println(message);
        return message;
    }

    /// Calculates the sum of two numbers.
    ///
    /// | Input | Result |
    /// |-------|--------|
    /// | 1, 2  | 3      |
    /// | 5, 5  | 10     |
    ///
    /// @param a the first number
    /// @param b the second number
    /// @return the sum of `a` and `b`
    public int add(int a, int b) {
        return a + b;
    }
}