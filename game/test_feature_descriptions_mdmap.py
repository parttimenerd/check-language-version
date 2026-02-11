import unittest

parse_feature_descriptions_mdmap = None


class FeatureDescriptionsMdmapTests(unittest.TestCase):

    @unittest.skip("feature-descriptions.mdmap parsing was removed; feature descriptions are now per-file markdown")
    def test_can_parse_current_mdmap(self):
        # The repository's mdmap currently contains template entries (AWT, APPLET) and rules.
        # This test ensures the parser can read it and validates the present blocks.
        parsed = parse_feature_descriptions_mdmap(
            'src/main/resources/me/bechberger/check/feature-descriptions.mdmap',
            feature_names=None  # test parsing without enum validation
        )
        self.assertIn('AWT', parsed)
        self.assertIn('APPLET', parsed)

        awt = parsed['AWT']
        self.assertIn('### Summary', awt['markdown'])
        self.assertIn('### Example', awt['markdown'])
        self.assertTrue(any(line.startswith('- [') for line in awt['links']))

    @unittest.skip("feature-descriptions.mdmap parsing was removed; feature descriptions are now per-file markdown")
    def test_rejects_bare_urls(self):
        md = """
@X
Summary: s

Details:
text

Example:
```java
// comment
class A {}
```

Historical:
N/A

Links:
- https://example.com
""".strip()

        with open('game/dist/_tmp_test.mdmap', 'w', encoding='utf-8') as f:
            f.write(md)

        with self.assertRaises(ValueError):
            parse_feature_descriptions_mdmap('game/dist/_tmp_test.mdmap')


if __name__ == '__main__':
    unittest.main()