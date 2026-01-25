// Java 15 feature: Text blocks
// Expected Version: 15
// Required Features: TEXT_BLOCKS
public class Java15_TextBlocks {
    public String getHtml() {
        return """
            <html>
                <body>Hello</body>
            </html>
            """;
    }
}
