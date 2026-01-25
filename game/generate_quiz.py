import os
import re
import json
import random
import urllib.request
import ssl

# Configuration
SOURCE_DIR = 'src/test/resources'
FEATURE_CHECKER_PATH = 'src/main/java/me/bechberger/check/FeatureChecker.java'
OUTPUT_DIR = 'game/dist'
OUTPUT_HTML = os.path.join(OUTPUT_DIR, 'index.html')
OUTPUT_DEPS_JS = os.path.join(OUTPUT_DIR, 'deps.js')
OUTPUT_CODE_JSON = os.path.join(OUTPUT_DIR, 'code.json')
MIN_LINES = 1
MAX_LINES = 20
MIN_VERSION = 1
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
    Removes // comments and /* ... */ comments from Java source.
    """
    # Regex for block comments /* ... */
    source = re.sub(r'/\*[\s\S]*?\*/', '', source)
    # Regex for single line comments // ...
    source = re.sub(r'//.*', '', source)
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

        # Collapse multiple empty lines into one
        code_pre = re.sub(r'\n{3,}', '\n\n', code_pre)

        # Remove leading and trailing empty lines
        code_pre = code_pre.strip()
        return code_pre

    # Ensure empty line between imports and class definition
    cleaned_code = re.sub(r'(import [^;]+;)\n(public |class |interface |enum |record |abstract |final |sealed )', r'\1\n\n\2', cleaned_code)

    # Collapse multiple empty lines into one
    cleaned_code = re.sub(r'\n{3,}', '\n\n', cleaned_code)

    # Remove leading and trailing empty lines
    return cleaned_code.strip()

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



def generate_html(questions):
    # Ensure output directory exists
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    # Download dependencies
    lemonade_js = download_lemonade()
    prism_css = download_url(PRISM_CSS_URL, "Error downloading Prism CSS")
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
    with open(OUTPUT_DEPS_JS, 'w', encoding='utf-8') as f:
        f.write(deps_content)
    print(f"Generated {OUTPUT_DEPS_JS}")

    # Write code.json (quiz data)
    with open(OUTPUT_CODE_JSON, 'w', encoding='utf-8') as f:
        json.dump(questions, f, indent=2)
    print(f"Generated {OUTPUT_CODE_JSON} with {len(questions)} questions.")

    # Read and render template
    with open('game/template.html', 'r', encoding='utf-8') as f:
        html_template = f.read()

    html = render_template(
        html_template,
        prism_css=prism_css
    )

    with open(OUTPUT_HTML, 'w', encoding='utf-8') as f:
        f.write(html)
    print(f"Generated {OUTPUT_HTML}")

if __name__ == "__main__":
    if not os.path.exists(SOURCE_DIR):
        print(f"Error: {SOURCE_DIR} does not exist.")
    else:
        qs = scan_files()
        if not qs:
            print("No valid questions found.")
        else:
            generate_html(qs)