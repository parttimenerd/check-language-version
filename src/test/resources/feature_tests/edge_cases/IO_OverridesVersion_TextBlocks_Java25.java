// Edge case: Copy of Java15_TextBlocks + unqualified IO usage in a compact source file.
// Expected Version: 25
// Required Features: TEXT_BLOCKS, IMPLICITLY_IMPORTED_IO_CLASS, IO_CLASS

class IO_OverridesVersion_TextBlocks_Java25 {
    public String getHtml() {
        return """
            <html>
                <body>Hello</body>
            </html>
            """;
    }

    public void printHtml() {
        String html = getHtml();
        IO.println(html);
    }
}