import unittest

from game.generate_quiz import sanitize_code


class SanitizeCodeTests(unittest.TestCase):
    def test_keeps_short_interface_name_and_renames_public_class_hint(self):
        src = """
interface I { default void m() {} }

public class Tiny_DefaultEmpty_Java8 implements I {}
""".strip()

        out = sanitize_code(src)

        # Interface name should remain I (it's not a hint)
        self.assertIn("interface I", out)

        # Public class has a hint-y name and should become Quiz
        self.assertIn("public class Quiz", out)
        self.assertNotIn("Tiny_DefaultEmpty_Java8", out)

        # Implements clause should still reference I
        self.assertIn("implements I", out)

    def test_does_not_rename_public_class_without_hints(self):
        src = """
public class HelloWorld {
    public static void main(String[] args) {}
}
""".strip()

        out = sanitize_code(src)
        self.assertIn("public class HelloWorld", out)
        self.assertNotIn("public class Quiz", out)

    def test_replaces_java_version_identifier_fragments(self):
        src = """
public class Java21Feature {
    int x;
}
""".strip()

        out = sanitize_code(src)
        # Java21Feature is a version-hint, so it should be sanitized to Quiz
        self.assertIn("public class Quiz", out)
        self.assertNotIn("Java21Feature", out)


if __name__ == "__main__":
    unittest.main()