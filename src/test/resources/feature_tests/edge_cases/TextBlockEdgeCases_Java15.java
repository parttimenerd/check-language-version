// Edge case: Text block variations
// Expected Version: 15
// Required Features: TEXT_BLOCKS
class TextBlockEdgeCases_Java15 {

    String basic = """
        Hello, World!
        """;

    String json = """
        {
            "name": "John",
            "age": 30
        }
        """;

    String sql = """
        SELECT id, name
        FROM users
        WHERE active = true
        """;

    String html = """
        <html>
            <body>
                <h1>Hello</h1>
            </body>
        </html>
        """;

    public String withFormatted(String name, int age) {
        return """
            Name: %s
            Age: %d
            """.formatted(name, age);
    }
}