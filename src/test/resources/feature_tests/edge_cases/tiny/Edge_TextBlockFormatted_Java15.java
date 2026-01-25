// Java 15 edge case: Text block with formatted method
// Test: Testing text blocks with the formatted() method
// Expected Version: 15
// Required Features: TEXT_BLOCKS
public class Edge_TextBlockFormatted_Java15 {
    String html = """
        <html>
            <body>
                <h1>%s</h1>
            </body>
        </html>
        """.formatted("Title");
}