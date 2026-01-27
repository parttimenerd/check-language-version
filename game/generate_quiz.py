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
OUTPUT_CODE_JSON = os.path.join(OUTPUT_DIR, 'code.json')
OUTPUT_PRISM_CSS = os.path.join(OUTPUT_DIR, 'prism.css')
MIN_LINES = 1
MAX_LINES = 20
MIN_VERSION = -2
MAX_VERSION = 25
LEMONADE_URL = 'https://lemonadejs.com/v5/lemonade.js'
PRISM_CSS_URL = 'https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism-tomorrow.min.css'
PRISM_JS_URL = 'https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/prism.min.js'
PRISM_JAVA_URL = 'https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-java.min.js'

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
    # Regex for single line comments // ... but NOT markdown doc comments /// ...
    source = re.sub(r'(?<!/)//((?!//).*)$', '', source, flags=re.MULTILINE)
    return source

def sanitize_code(code):
    """
    1. Removes package declaration.
    2. Renames first top-level type to Quiz.
    3. Replaces all class/type names that contain Java version hints.
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
    # Pattern: word boundary, any word chars, then Java + digits, then any word chars, word boundary
    cleaned_code = re.sub(r'\b(\w*_)?Java\d+(_\w*)?\b', 'Example', cleaned_code)
    cleaned_code = re.sub(r'\bJava\d+_\w+\b', 'Example', cleaned_code)
    cleaned_code = re.sub(r'\b\w+_Java\d+\b', 'Example', cleaned_code)

    # Also replace identifiers like: Tiny_..., Edge_..., Combo_... that might hint at test type
    cleaned_code = re.sub(r'\bTiny_\w*', 'Example', cleaned_code)
    cleaned_code = re.sub(r'\bEdge_\w*', 'Example', cleaned_code)
    cleaned_code = re.sub(r'\bCombo_\w*', 'Example', cleaned_code)
    cleaned_code = re.sub(r'\bMinimal\w*', 'Example', cleaned_code)

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

    # Find top-level type definition (class, interface, enum, record)
    type_pattern = re.compile(r'(public\s+|private\s+|protected\s+)?(class|interface|enum|record)\s+(\w+)')
    match = type_pattern.search(cleaned_code)

    if match:
        original_name = match.group(3)

        start_idx = match.start(3)
        end_idx = match.end(3)

        # Replace name in definition
        code_pre = cleaned_code[:start_idx] + "Quiz" + cleaned_code[end_idx:]

        # Also replace constructor name if it's a class/record
        if match.group(2) in ['class', 'record']:
             code_pre = re.sub(fr'\b{original_name}\s*\(', 'Quiz(', code_pre)

        # Ensure empty line between imports and class definition
        code_pre = re.sub(r'(import [^;]+;)\n(public |class |interface |enum |record |abstract |final |sealed )', r'\1\n\n\2', code_pre)

        # Normalize empty lines
        return normalize_empty_lines(code_pre)

    # Ensure empty line between imports and class definition
    cleaned_code = re.sub(r'(import [^;]+;)\n(public |class |interface |enum |record |abstract |final |sealed )', r'\1\n\n\2', cleaned_code)

    # Normalize empty lines
    return normalize_empty_lines(cleaned_code)

def scan_files():
    feature_map = parse_feature_enum(FEATURE_CHECKER_PATH)
    questions = []

    for root, dirs, files in os.walk(SOURCE_DIR):
        for file in files:
            if not file.endswith('.java'):
                continue

            filepath = os.path.join(root, file)

            with open(filepath, 'r', encoding='utf-8') as f:
                raw_content = f.read()

            # extract metadata
            expected_version = None
            required_features_str = None

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
            file_features = []
            if feat_match:
                feats_list = feat_match.group(1).split() # split by whitespace (and commas probably?)
                # Cleaning up potential commas
                feats_list = [f.strip(',').strip() for f in feats_list]

                for f_name in feats_list:
                    if f_name in feature_map:
                        file_features.append({
                            'name': f_name,
                            'label': feature_map[f_name]['label'],
                            'version': feature_map[f_name]['version']
                        })
                    else:
                        file_features.append({
                            'name': f_name,
                            'label': f_name,
                            'version': '?'
                        })

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

            # Prepare question (options generated in UI)
            question = {
                'code': sanitized,
                'correct': expected_version,
                'features': file_features
            }
            questions.append(question)

    return questions


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

    # === Dependencies: download once and cache in OUTPUT_DIR ===
    # If deps.js already exists in OUTPUT_DIR, reuse it and don't re-download JS libs
    deps_js_cached = os.path.exists(OUTPUT_DEPS_JS)

    if deps_js_cached:
        print(f"Using cached {OUTPUT_DEPS_JS}; skipping JS dependency downloads.")
    else:
        # Download dependencies
        lemonade_js = download_lemonade()
        prism_js = download_url(PRISM_JS_URL, "Error downloading Prism JS")
        prism_java = download_url(PRISM_JAVA_URL, "Error downloading Prism Java")

        # Write deps.js (LemonadeJS + PrismJS)
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

    # Write code.json (quiz data)
    with open(OUTPUT_CODE_JSON, 'w', encoding='utf-8') as f:
        json.dump(questions, f, indent=2)
    print(f"Generated {OUTPUT_CODE_JSON} with {len(questions)} questions.")

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
                        help='Weight multiplier for alpha version questions (default: 1). '
                             'Use 0 to exclude alpha questions, >1 to increase their frequency.')
    parser.add_argument('--test', action='store_true',
                        help='Test compile all alpha_feature tests via javac (in temporary folders).')
    parser.add_argument('--base-url', type=str, metavar='URL',
                        help='Base URL to prefix to relative asset paths (e.g., https://example.com/).')
    parser.add_argument('--goatcounter', type=str, metavar='URL',
                        help='GoatCounter URL for analytics (e.g., https://example.goatcounter.com/count).')
    args = parser.parse_args()

    # If --test is specified, run tests and exit
    if args.test:
        success = test_alpha_questions()
        exit(0 if success else 1)

    if not os.path.exists(SOURCE_DIR):
        print(f"Error: {SOURCE_DIR} does not exist.")
    else:
        qs = scan_files()

        # Load and merge alpha questions with weight
        if args.alpha_weight > 0:
            alpha_qs = load_alpha_questions()
            # Duplicate alpha questions based on weight
            for _ in range(args.alpha_weight):
                qs.extend(alpha_qs)
            print(f"Added {len(alpha_qs) * args.alpha_weight} alpha questions (weight={args.alpha_weight})")

        if not qs:
            print("No valid questions found.")
        else:
            generate_html(qs, goatcounter_url=args.goatcounter, base_url=(args.base_url or ''))