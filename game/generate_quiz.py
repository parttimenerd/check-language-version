import os
import re
import json
import random
import urllib.request
import ssl
import argparse
import tempfile
import subprocess
import shutil

# Configuration
SOURCE_DIR = 'src/test/resources'
FEATURE_CHECKER_PATH = 'src/main/java/me/bechberger/check/FeatureChecker.java'
OUTPUT_DIR = 'game/dist'
OUTPUT_HTML = os.path.join(OUTPUT_DIR, 'index.html')
OUTPUT_DEPS_JS = os.path.join(OUTPUT_DIR, 'deps.js')
# Late-loaded deps (only needed on answer/explanation screen)
OUTPUT_DEPS_MD_JS = os.path.join(OUTPUT_DIR, 'deps_md.js')
OUTPUT_CODE_JSON = os.path.join(OUTPUT_DIR, 'code.json')
# Late-loaded feature metadata (labels/versions/descriptions)
OUTPUT_DESCRIPTIONS_JSON = os.path.join(OUTPUT_DIR, 'descriptions.json')
OUTPUT_PRISM_CSS = os.path.join(OUTPUT_DIR, 'prism.css')
MIN_LINES = 1
MAX_LINES = 18
MIN_VERSION = -3
MAX_VERSION = 25
# LemonadeJS (UI framework)
# Prefer jsDelivr (GitHub-backed) to avoid 403/404 issues.
LEMONADE_URL = 'https://cdn.jsdelivr.net/gh/lemonadejs/lemonadejs@v5.0.4/dist/lemonade.min.js'
PRISM_CSS_URL = 'https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism-tomorrow.min.css'
PRISM_JS_URL = 'https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/prism.min.js'
PRISM_JAVA_URL = 'https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-java.min.js'
# Markdown renderer (used in the answer screen for feature descriptions)
MARKDOWN_IT_URL = 'https://cdnjs.cloudflare.com/ajax/libs/markdown-it/13.0.1/markdown-it.min.js'

# New format: one markdown file per feature
FEATURES_DIR = 'src/main/resources/me/bechberger/check/features'

# --------------------
# Feature descriptions loader + validators
# --------------------

# Accept labels containing nested brackets (e.g., [`Type.method()`]) by matching greedily up to the last closing bracket before the URL.
_MD_LABELED_LINK_RE = re.compile(r'\[(.+?)\]\((https?://[^\s)]+)\)')
# Disallow bare URLs anywhere outside of markdown link targets
# (From rules: (?<!\])https?://[^\s)]+)
_MD_BARE_URL_RE = re.compile(r'(?<!\])https?://[^\s)]+')

def _normalize_newlines(s: str) -> str:
    return s.replace('\r\n', '\n').replace('\r', '\n')


def _validate_feature_markdown(feature_name: str, md: str) -> None:
    """Validate a single feature markdown file.

    Rules are documented in `feature-descriptions.mdmap`.

    We validate:
    - required section headers exist and are H3: Summary/Details/Example/Historical/Links
    - Links section has at least one labeled markdown link
    - Example section has exactly one fenced code block with a language tag and includes a comment

    Note: We intentionally do NOT fail or warn on bare URLs here; link/bare-URL checks are handled elsewhere.
    """
    md = _normalize_newlines(md)

    # NOTE: Bare URL validation intentionally disabled for generate_quiz.py

    # Required H3 sections in order
    required_headers = [
        '### Summary',
        '### Details',
        '### Example',
        '### Historical',
        '### Links',
    ]

    header_positions = []
    for h in required_headers:
        pos = md.find(h)
        if pos < 0:
            raise ValueError(f"Feature {feature_name}: missing required header {h}")
        header_positions.append(pos)

    if header_positions != sorted(header_positions):
        raise ValueError(f"Feature {feature_name}: headers are not in required order: {required_headers}")

    # Helper: slice section body between headers
    def section_body(start_h: str, end_h: str | None) -> str:
        start = md.find(start_h)
        if start < 0:
            return ''
        start = start + len(start_h)
        end = len(md) if end_h is None else md.find(end_h, start)
        if end < 0:
            end = len(md)
        return md[start:end].strip()

    example = section_body('### Example', '### Historical')
    links = section_body('### Links', None)

    # Example: exactly one fenced code block with language tag
    fence_starts = [m.start() for m in re.finditer(r'^```[A-Za-z0-9_-]+\s*$', example, flags=re.MULTILINE)]
    fence_ends = [m.start() for m in re.finditer(r'^```\s*$', example, flags=re.MULTILINE)]
    if len(fence_starts) != 1 or len(fence_ends) != 1:
        raise ValueError(f"Feature {feature_name}: Example must contain exactly one fenced code block with language tag")

    if ('//' not in example) and ('/*' not in example):
        raise ValueError(f"Feature {feature_name}: Example code block must contain a comment")

    # Links: at least one labeled markdown link in a bullet list
    link_lines = [ln.strip() for ln in links.split('\n') if ln.strip()]
    if not link_lines:
        raise ValueError(f"Feature {feature_name}: Links section is empty")
    for ln in link_lines:
        if not ln.startswith('- '):
            raise ValueError(f"Feature {feature_name}: Links line must start with '- ': {ln}")
        if not _MD_LABELED_LINK_RE.search(ln):
            raise ValueError(f"Feature {feature_name}: Links must be labeled markdown links: {ln}")


def load_feature_markdown_files(feature_names: set[str] | None = None) -> dict[str, str]:
    """Load per-feature markdown descriptions.

    Returns a mapping FEATURE_NAME -> markdown string.

    If feature_names is provided, we only load those files and we error if the file is missing.
    """
    if not os.path.isdir(FEATURES_DIR):
        print(f"Warning: {FEATURES_DIR} not found. Feature descriptions will be missing.")
        return {}

    out: dict[str, str] = {}

    if feature_names is None:
        md_files = [f for f in os.listdir(FEATURES_DIR) if f.endswith('.md')]
        for fn in md_files:
            name = fn[:-3]
            path = os.path.join(FEATURES_DIR, fn)
            with open(path, 'r', encoding='utf-8') as f:
                md = f.read()
            _validate_feature_markdown(name, md)
            out[name] = _normalize_newlines(md).strip() + "\n"
        return out

    # Strict mode: require all enum features to have a file.
    for name in sorted(feature_names):
        path = os.path.join(FEATURES_DIR, f"{name}.md")
        if not os.path.exists(path):
            raise FileNotFoundError(f"Missing feature description file: {path}")
        with open(path, 'r', encoding='utf-8') as f:
            md = f.read()
        _validate_feature_markdown(name, md)
        out[name] = _normalize_newlines(md).strip() + "\n"

    return out


def parse_feature_enum(checker_path):
    """
    Parses FeatureChecker.java to map feature enum constant -> (version, label).
    Returns a dict: { 'VAR': {'version': 10, 'label': 'Local variable type inference (var)'}, ... }
    """
    features = {}
    if not os.path.exists(checker_path):
        print(f"Warning: {checker_path} not found. Feature details may be missing.")
        return features

    with open(checker_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Regex to find enum constants: NAME(version, boolean, "label"...)
    # Example: AWT(1, true, "java.awt"), or INNER_CLASSES(1, false, "Inner classes"),
    # or VAR(10, false, "Local variable type inference (var)")

    # We look for the enum block first
    enum_match = re.search(r'public enum JavaFeature\s*\{(.*?)\}', content, re.DOTALL)
    if not enum_match:
        return features

    enum_body = enum_match.group(1)

    # Pattern to match: CONSTANT(version, ..., "label")
    pattern = re.compile(
        r'([A-Z0-9_]+)\s*\(\s*(\d+)\s*,\s*(?:true|false)\s*,\s*"([^"]+)"'
    )

    for match in pattern.finditer(enum_body):
        name = match.group(1)
        version = int(match.group(2))
        label = match.group(3)
        features[name] = {'version': version, 'label': label}

    return features

def remove_comments(source):
    """
    Removes // comments and /* ... */ comments from Java source,
    but preserves markdown doc comments (/// ...).
    """
    # Regex for block comments /* ... */
    source = re.sub(r'/\*[\s\S]*?\*/', '', source)
    # Regex for single line comments starting with //, but NOT markdown doc comments /// ...
    # Keep any line whose comment marker is exactly '///' (Java 23 markdown doc comments, JEP 467).
    source = re.sub(r'(?m)^(\s*)//(?!/)\s?.*$', r'\1', source)
    return source

def sanitize_code(code):
    """
    1. Removes package declaration.
    2. Renames first *public* top-level type to Quiz (preferring class/record over interface/enum).
    3. Replaces class/type names that contain Java version/test-suite hints.

    Goal: hide hint-y names like `Tiny_DefaultEmpty_Java8`, but keep innocuous short names like `I`.
    """
    lines = code.split('\n')
    new_lines = []

    # Remove package declaration
    for line in lines:
        if line.strip().startswith('package '):
            continue
        new_lines.append(line)

    cleaned_code = '\n'.join(new_lines)

    # Replace all identifiers that contain Java version patterns (Java followed by digits)
    # This handles: Java5_Foo, Foo_Java11, Java21Feature, Tiny_Foo_Java16, etc.
    # We need to be careful not to replace things like "java.util" (lowercase)
    # Also: don't replace short identifiers (<5 chars) unless they contain digits.
    def _replace_version_hint_identifier(m: re.Match) -> str:
        ident = m.group(0)
        # if no digits and very short (likely an interface like I/A/B), keep it
        if len(ident) < 5 and not re.search(r'\d', ident):
            return ident
        return 'Example'

    # Pattern: word boundary, optional prefix_, Java + digits, optional _suffix, word boundary
    cleaned_code = re.sub(r'\b(\w*_)?Java\d+(_\w*)?\b', _replace_version_hint_identifier, cleaned_code)

    # Also replace identifiers like: Tiny_..., Edge_..., Combo_... that might hint at test type
    # but keep very short identifiers without digits intact.
    def _replace_test_prefix(m: re.Match) -> str:
        ident = m.group(0)
        if len(ident) < 5 and not re.search(r'\d', ident):
            return ident
        return 'Example'

    cleaned_code = re.sub(r'\bTiny_\w*', _replace_test_prefix, cleaned_code)
    cleaned_code = re.sub(r'\bEdge_\w*', _replace_test_prefix, cleaned_code)
    cleaned_code = re.sub(r'\bCombo_\w*', _replace_test_prefix, cleaned_code)
    cleaned_code = re.sub(r'\bMinimal\w*', _replace_test_prefix, cleaned_code)

    def normalize_empty_lines(code):
        """Remove excessive empty lines, keeping at most one empty line between code blocks."""
        # Split into lines
        lines = code.split('\n')
        result = []
        prev_empty = False
        for line in lines:
            is_empty = line.strip() == ''
            if is_empty:
                if not prev_empty:
                    result.append('')
                prev_empty = True
            else:
                result.append(line)
                prev_empty = False
        # Join and strip leading/trailing empty lines
        return '\n'.join(result).strip()

    # Find top-level PUBLIC type definition.
    # Prefer renaming a public class/record (common file main type), otherwise public interface/enum.
    # NOTE: version-hint sanitization above may already have replaced the original name with "Example".
    public_type_patterns = [
        re.compile(r'\bpublic\s+(class|record)\s+(\w+)'),
        re.compile(r'\bpublic\s+(interface|enum)\s+(\w+)'),
    ]

    match = None
    for p in public_type_patterns:
        match = p.search(cleaned_code)
        if match:
            break

    if match:
        kind = match.group(1)
        original_name = match.group(2)

        def _is_hint_name(name: str) -> bool:
            """Return True if `name` likely leaks version/test-suite hints and should be sanitized."""
            if not name:
                return False
            if name == 'Quiz':
                return False
            # Never rename innocuous, short names like I/A/B.
            if len(name) < 5 and not re.search(r'\d', name):
                return False
            # Otherwise rename only when it looks like a hint.
            return (
                bool(re.search(r'Java\d+', name))
                or name.startswith(('Tiny_', 'Edge_', 'Combo_', 'Minimal'))
                # If a previous pass already replaced the original hint-y name, treat it as hint-y too.
                or name == 'Example'
            )

        # We only rename the top-level type if its name is hint-y.
        should_rename_top_level = _is_hint_name(original_name)

        if should_rename_top_level:
            # Replace name in definition (match group 2 spans the identifier)
            start_idx = match.start(2)
            end_idx = match.end(2)
            code_pre = cleaned_code[:start_idx] + 'Quiz' + cleaned_code[end_idx:]

            # Replace the original top-level type name everywhere else too (extends/implements/type uses/etc.).
            code_pre = re.sub(fr'\b{re.escape(original_name)}\b', 'Quiz', code_pre)

            # Also replace constructor name if it's a class/record
            if kind in ['class', 'record']:
                code_pre = re.sub(fr'\b{re.escape(original_name)}\s*\(', 'Quiz(', code_pre)
        else:
            # Keep the original top-level type name.
            code_pre = cleaned_code

        # Ensure empty line between imports and class definition
        code_pre = re.sub(
            r'(import [^;]+;)\n(public |class |interface |enum |record |abstract |final |sealed )',
            r'\1\n\n\2',
            code_pre,
        )

        return normalize_empty_lines(code_pre)

    # Ensure empty line between imports and class definition
    cleaned_code = re.sub(r'(import [^;]+;)\n(public |class |interface |enum |record |abstract |final |sealed )', r'\1\n\n\2', cleaned_code)

    # Normalize empty lines
    return normalize_empty_lines(cleaned_code)

def scan_files():
    """Scan SOURCE_DIR and return quiz payload ({features, entries})."""
    feature_map = parse_feature_enum(FEATURE_CHECKER_PATH)

    # Load per-feature markdown descriptions and merge into feature metadata.
    feature_markdown: dict[str, str] = {}
    try:
        feature_markdown = load_feature_markdown_files(set(feature_map.keys()) if feature_map else None)
    except FileNotFoundError as e:
        # Fail fast: keeping descriptions in sync is required for the game.
        raise

    features_out: dict[str, dict] = {}
    for name, info in feature_map.items():
        features_out[name] = {
            'label': info.get('label'),
            'description': feature_markdown.get(name, ''),
            'version': info.get('version'),
        }

    entries = []

    for root, dirs, files in os.walk(SOURCE_DIR):
        for file in files:
            if not file.endswith('.java'):
                continue

            filepath = os.path.join(root, file)

            with open(filepath, 'r', encoding='utf-8') as f:
                raw_content = f.read()

            # extract metadata
            expected_version = None

            # 1. Header comment Expected Version: N
            ver_match = re.search(r'Expected Version:\s*(\d+)', raw_content)
            if ver_match:
                expected_version = int(ver_match.group(1))
            else:
                # 2. Filename fallback
                file_ver_match = re.search(r'Java(\d+)', file)
                if file_ver_match:
                    expected_version = int(file_ver_match.group(1))

            if expected_version is None:
                continue

            # Extract required features
            # Format: // Required Features: A, B, C
            feat_match = re.search(r'Required Features:\s*(.*)', raw_content)
            file_feature_names: list[str] = []
            required_features_text = None
            if feat_match:
                required_features_text = (feat_match.group(1) or '').strip()
                feats_list = required_features_text.split()  # split by whitespace
                # Cleaning up potential commas
                feats_list = [f.strip(',').strip() for f in feats_list]

                for f_name in feats_list:
                    if not f_name:
                        continue
                    if f_name in feature_map:
                        file_feature_names.append(f_name)

            # If the file explicitly declares no required features, treat it as Java 1.0-alpha1 (-3)
            # for the game dataset, regardless of filename/Expected Version.
            if required_features_text is not None and required_features_text == '':
                expected_version = -3

            # Check logic range
            if not (MIN_VERSION <= expected_version <= MAX_VERSION):
                continue

            # Preprocessing
            content_no_comments = remove_comments(raw_content)

            # Count non-empty lines
            lines = [l for l in content_no_comments.split('\n') if l.strip()]
            line_count = len(lines)

            if not (MIN_LINES <= line_count <= MAX_LINES):
                continue

            # Remove comments and sanitize the code
            code_without_comments = remove_comments(raw_content)
            sanitized = sanitize_code(code_without_comments)

            # Double check content is not empty after sanitizing?
            if not sanitized.strip():
                continue

            # Prepare entry (options generated in UI)
            entry = {
                'code': sanitized,
                'correct': expected_version,
                'features': file_feature_names,
            }
            entries.append(entry)

    return {
        'features': features_out,
        'entries': entries,
    }


def load_alpha_questions():
    """
    Loads hand-crafted alpha version questions from alpha_features.json.
    These are historical Java 1.0-alpha2 (-2) and 1.0-alpha3 (-1) examples.
    """
    alpha_path = os.path.join(os.path.dirname(__file__), 'alpha_features.json')
    if not os.path.exists(alpha_path):
        print(f"Warning: {alpha_path} not found. Alpha questions will not be included.")
        return []

    with open(alpha_path, 'r', encoding='utf-8') as f:
        questions = json.load(f)

    print(f"Loaded {len(questions)} alpha version questions.")
    return questions

def load_alpha1_questions():
    """Loads hand-crafted Java 1.0-alpha1 questions (version -3) with no detected features."""
    alpha1_path = os.path.join(os.path.dirname(__file__), 'alpha1_features.json')
    if not os.path.exists(alpha1_path):
        return []
    with open(alpha1_path, 'r', encoding='utf-8') as f:
        questions = json.load(f)
    print(f"Loaded {len(questions)} alpha1 version questions.")
    return questions


def test_alpha_questions():
    """
    Test compiles all alpha_feature tests via javac in temporary folders.
    Returns True if all tests pass, False otherwise.
    """
    alpha_qs = load_alpha_questions()
    if not alpha_qs:
        print("No alpha questions to test.")
        return True

    # Check if javac is available
    try:
        result = subprocess.run(['javac', '-version'], capture_output=True, text=True)
        print(f"Using {result.stderr.strip() or result.stdout.strip()}")
    except FileNotFoundError:
        print("Error: javac not found. Please install JDK.")
        return False

    passed = 0
    failed = 0
    failures = []

    print(f"\nTesting {len(alpha_qs)} alpha questions...\n")

    for i, question in enumerate(alpha_qs):
        code = question['code']
        version = question['correct']
        features = [f['label'] for f in question.get('features', [])]

        # Create a temporary directory for this test
        temp_dir = tempfile.mkdtemp(prefix=f'alpha_test_{i}_')

        try:
            # Write the Java file
            java_file = os.path.join(temp_dir, 'Quiz.java')
            with open(java_file, 'w', encoding='utf-8') as f:
                f.write(code)

            # Compile with javac
            # Use source/target 1.8 as minimum that supports all the alpha syntax
            result = subprocess.run(
                ['javac', '-source', '1.8', '-target', '1.8', '-Xlint:-options', java_file],
                capture_output=True,
                text=True,
                cwd=temp_dir
            )

            if result.returncode == 0:
                passed += 1
                version_str = "1.0-α3" if version == -1 else "1.0-α2" if version == -2 else str(version)
                print(f"  ✓ Test {i+1}: {version_str} - {', '.join(features)}")
            else:
                failed += 1
                version_str = "1.0-α3" if version == -1 else "1.0-α2" if version == -2 else str(version)
                failures.append({
                    'index': i + 1,
                    'version': version_str,
                    'features': features,
                    'error': result.stderr,
                    'code': code
                })
                print(f"  ✗ Test {i+1}: {version_str} - {', '.join(features)}")
        finally:
            # Clean up the temporary directory
            shutil.rmtree(temp_dir, ignore_errors=True)

    print(f"\n{'='*50}")
    print(f"Results: {passed} passed, {failed} failed out of {len(alpha_qs)} tests")

    if failures:
        print(f"\n{'='*50}")
        print("FAILURES:\n")
        for f in failures:
            print(f"Test {f['index']} ({f['version']}):")
            print(f"  Features: {', '.join(f['features'])}")
            print(f"  Error:\n{f['error']}")
            print(f"  Code:\n{f['code']}\n")
            print("-" * 40)

    return failed == 0


def download_lemonade():
    print(f"Downloading LemonadeJS from {LEMONADE_URL}...")
    try:
        # Create an unverified SSL context to avoid certificate verification errors
        ctx = ssl.create_default_context()
        ctx.check_hostname = False
        ctx.verify_mode = ssl.CERT_NONE

        with urllib.request.urlopen(LEMONADE_URL, context=ctx) as response:
            return response.read().decode('utf-8')
    except Exception as e:
        print(f"Error downloading LemonadeJS: {e}")
        # Fallback to a minimal dummy if download fails (so script doesn't crash completely)
        return "/* Error downloading LemonadeJS */"

def download_url(url, error_msg):
    print(f"Downloading {url}...")
    try:
        ctx = ssl.create_default_context()
        ctx.check_hostname = False
        ctx.verify_mode = ssl.CERT_NONE
        with urllib.request.urlopen(url, context=ctx) as response:
            return response.read().decode('utf-8')
    except Exception as e:
        print(f"Error downloading {url}: {e}")
        return f"/* {error_msg} */"

def render_template(template, **kwargs):
    """
    Tiny template engine.
    Replaces {{% key %}} with value from kwargs.
    """
    for key, value in kwargs.items():
        template = template.replace(f'{{%{key}%}}', str(value))
    return template



def generate_html(questions, goatcounter_url=None, base_url=''):
    # Ensure output directory exists
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    # Normalize base_url: ensure empty string or ends with a single '/'
    if base_url:
        if not base_url.endswith('/'):
            base_url = base_url + '/'
    else:
        base_url = ''
    # If caller provided a base_url without scheme (common mistake), don't try to fix it here,
    # but the generated share links will likely be wrong.

    # === Dependencies: download once and cache in OUTPUT_DIR ===
    # We split deps into:
    # - deps.js      : LemonadeJS + Prism (needed for initial render)
    # - deps_md.js   : markdown-it (only needed when opening feature explanations)
    deps_core_cached = os.path.exists(OUTPUT_DEPS_JS)
    deps_md_cached = os.path.exists(OUTPUT_DEPS_MD_JS)

    if deps_core_cached and deps_md_cached:
        print(f"Using cached {OUTPUT_DEPS_JS} and {OUTPUT_DEPS_MD_JS}; skipping JS dependency downloads.")
    else:
        # Download dependencies as needed
        lemonade_js = None
        prism_js = None
        prism_java = None
        markdown_it = None

        if not deps_core_cached:
            lemonade_js = download_lemonade()
            prism_js = download_url(PRISM_JS_URL, "Error downloading Prism JS")
            prism_java = download_url(PRISM_JAVA_URL, "Error downloading Prism Java")

            deps_content = f"""// Dependencies: LemonadeJS + PrismJS
// Auto-generated - do not edit

// === LemonadeJS ===
{lemonade_js}

// === PrismJS Core ===
{prism_js}

// === PrismJS Java Language ===
{prism_java}
"""
            try:
                with open(OUTPUT_DEPS_JS, 'w', encoding='utf-8') as f:
                    f.write(deps_content)
                print(f"Generated {OUTPUT_DEPS_JS}")
            except Exception as e:
                print(f"Error writing {OUTPUT_DEPS_JS}: {e}")
        else:
            print(f"Using cached {OUTPUT_DEPS_JS}; skipping core JS dependency downloads.")

        if not deps_md_cached:
            markdown_it = download_url(MARKDOWN_IT_URL, "Error downloading markdown-it")

            deps_md_content = f"""// Dependency: markdown-it
// Auto-generated - do not edit

// === markdown-it ===
{markdown_it}
"""
            try:
                with open(OUTPUT_DEPS_MD_JS, 'w', encoding='utf-8') as f:
                    f.write(deps_md_content)
                print(f"Generated {OUTPUT_DEPS_MD_JS}")
            except Exception as e:
                print(f"Error writing {OUTPUT_DEPS_MD_JS}: {e}")
        else:
            print(f"Using cached {OUTPUT_DEPS_MD_JS}; skipping markdown-it download.")

    # Prism CSS: cache separately (used in template <style>)
    if os.path.exists(OUTPUT_PRISM_CSS):
        try:
            with open(OUTPUT_PRISM_CSS, 'r', encoding='utf-8') as f:
                prism_css = f.read()
            print(f"Using cached {OUTPUT_PRISM_CSS}; skipping Prism CSS download.")
        except Exception as e:
            print(f"Error reading cached Prism CSS: {e}. Falling back to download.")
            prism_css = download_url(PRISM_CSS_URL, "Error downloading Prism CSS")
            try:
                with open(OUTPUT_PRISM_CSS, 'w', encoding='utf-8') as f:
                    f.write(prism_css)
                print(f"Cached Prism CSS to {OUTPUT_PRISM_CSS}")
            except Exception as e2:
                print(f"Error caching Prism CSS: {e2}")
    else:
        prism_css = download_url(PRISM_CSS_URL, "Error downloading Prism CSS")
        try:
            with open(OUTPUT_PRISM_CSS, 'w', encoding='utf-8') as f:
                f.write(prism_css)
            print(f"Cached Prism CSS to {OUTPUT_PRISM_CSS}")
        except Exception as e:
            print(f"Error caching Prism CSS: {e}")

    # If deps.js was cached, we don't need to set lemonade_js/prism_js/prism_java variables here

    # Write quiz payload split into:
    # - code.json          : only the entries (needed at startup)
    # - descriptions.json  : feature metadata (needed only when showing explanations)
    if not isinstance(questions, dict) or 'entries' not in questions:
        raise ValueError("Expected questions to be a dict with keys {'features','entries'}")

    with open(OUTPUT_CODE_JSON, 'w', encoding='utf-8') as f:
        json.dump({'entries': questions.get('entries', [])}, f, indent=2)
    entries_count = len(questions.get('entries', []))
    print(f"Generated {OUTPUT_CODE_JSON} with {entries_count} questions.")

    with open(OUTPUT_DESCRIPTIONS_JSON, 'w', encoding='utf-8') as f:
        json.dump({'features': questions.get('features', {})}, f, indent=2)
    print(f"Generated {OUTPUT_DESCRIPTIONS_JSON} with {len(questions.get('features', {}))} features.")

    # Read and render template
    with open('game/template.html', 'r', encoding='utf-8') as f:
        html_template = f.read()

    # Generate GoatCounter script if URL provided
    if goatcounter_url:
        goatcounter_script = f'''<script data-goatcounter="{goatcounter_url}"
        async src="{goatcounter_url.replace('/count', '/count.js')}"></script>'''
    else:
        goatcounter_script = ''

    html = render_template(
        html_template,
        prism_css=prism_css,
        goatcounter_script=goatcounter_script,
        base_url=base_url
    )


    with open(OUTPUT_HTML, 'w', encoding='utf-8') as f:
        f.write(html)
    print(f"Generated {OUTPUT_HTML}")

    # Copy screenshot image (if present) into dist/img/screenshot.png
    src_img = os.path.join(os.path.dirname(__file__), 'img', 'screenshot.png')
    dest_img_dir = os.path.join(OUTPUT_DIR, 'img')
    if os.path.exists(src_img):
        try:
            os.makedirs(dest_img_dir, exist_ok=True)
            shutil.copy2(src_img, os.path.join(dest_img_dir, 'screenshot.png'))
            print(f"Copied screenshot to {os.path.join(dest_img_dir, 'screenshot.png')}")
        except Exception as e:
            print(f"Error copying screenshot: {e}")
    else:
        print(f"Warning: {src_img} not found. No screenshot copied.")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Generate Java Version Quiz')
    parser.add_argument('--alpha-weight', type=int, default=1, metavar='N',
                        help='Weight multiplier for alpha version questions (default: 1 = shipped once). '
                             'Use 0 to exclude them from code.json, or >1 to increase their frequency.')
    parser.add_argument('--test', action='store_true',
                        help='Test compile all alpha_feature tests via javac (in temporary folders).')
    parser.add_argument('--base-url', type=str, required=True, metavar='URL',
                        help='Required. Base URL to prefix to relative asset paths and for share links (e.g., https://example.com/quiz/).')
    parser.add_argument('--goatcounter', type=str, metavar='URL',
                        help='GoatCounter URL for analytics (e.g., https://example.goatcounter.com/count).')
    args = parser.parse_args()
    if not args.base_url.strip():
        raise SystemExit('--base-url must not be empty')

    # If --test is specified, run tests and exit
    if args.test:
        success = test_alpha_questions()
        exit(0 if success else 1)

    if not os.path.exists(SOURCE_DIR):
        print(f"Error: {SOURCE_DIR} does not exist.")
    else:
        qs = scan_files()

        # Load feature markdown once for use in alpha-only feature metadata.
        # (scan_files() loads it internally; we also need it here when merging alpha entries.)
        feature_map = parse_feature_enum(FEATURE_CHECKER_PATH)
        feature_markdown = load_feature_markdown_files(set(feature_map.keys()) if feature_map else None)

        # Load hand-crafted alpha questions too (keep compatibility)
        alpha_weight = getattr(args, 'alpha_weight', 1)
        if alpha_weight > 0:
            alpha_qs = load_alpha_questions()
            alpha1_qs = load_alpha1_questions()

            # Old alpha jsons have per-question feature objects; map to feature-name lists.
            def _normalize_alpha(q):
                feats = q.get('features', [])
                if isinstance(feats, list) and feats and isinstance(feats[0], dict):
                    q = dict(q)
                    q['features'] = [f.get('name') for f in feats if f.get('name')]
                return q

            alpha_entries = [_normalize_alpha(q) for q in (alpha_qs + alpha1_qs)]

            # Ensure feature registry has any alpha features that are referenced.
            for q in alpha_entries:
                for name in q.get('features', []) or []:
                    if name and name not in qs['features']:
                        # Alpha-only / historical feature: keep minimal metadata.
                        qs['features'][name] = {
                            'label': name,
                            'description': feature_markdown.get(name, ''),
                            'version': q.get('correct', -3),
                        }

            # Add alpha entries according to weight.
            # If weight==1: include once, if >1 duplicates.
            qs['entries'].extend(alpha_entries * alpha_weight)

        # Shuffle after merging
        random.shuffle(qs['entries'])

        os.makedirs(OUTPUT_DIR, exist_ok=True)
        generate_html(qs, goatcounter_url=args.goatcounter, base_url=args.base_url)