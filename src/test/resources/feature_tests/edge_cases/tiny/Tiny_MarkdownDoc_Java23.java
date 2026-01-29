// Test: Markdown doc comments (Java 23)
// Expected Version: 23
// Required Features: MARKDOWN_DOC_COMMENTS
// Compile Check: false
/// # Title
/// This is **markdown** documentation.
/// - Item 1
/// - Item 2
class Tiny_MarkdownDoc_Java23 {
    /// Returns the **sum** of `a` and `b`.
    public int add(int a, int b) { return a + b; }
}