#!/usr/bin/env python3
"""
Simple helper to parse test-suite error output and interactively add missing features
to test specification files.

Usage:

  cat errors.txt | python3 tools/add_missing_features.py

Or:
  python3 tools/add_missing_features.py errors.txt

It understands two kinds of lines:

1) Missing feature errors like:
     Detected feature 'SWITCH_EXPRESSIONS' is not listed in required or optional features for Foo.java.

2) Version mismatch errors like:
     Expected Java 25, but detected Java 23 for Foo.java. Detected features: [A, B, C]

For (1) it will add missing features.
For (2) it can optionally REPLACE the Required Features list with the detected features
(so it can remove outdated features too).

The script creates a .bak backup before modifying files.
"""

import argparse
import os
import re
import shutil
import sys
from collections import defaultdict

FEATURE_LINE_RE = re.compile(r"Detected feature '([^']+)' is not listed .* for ([\w\-_.]+\.java)\.")

# Example JUnit line (wrapped by Maven Surefire):
# [ERROR]   FeatureDetectionTest.testFeatureDetection:310 Expected Java 25, but detected Java 23 for Foo.java.
# Detected features: [A, B, C] ==> expected: <25> but was: <23>
VERSION_MISMATCH_RE = re.compile(
    r"Expected Java \d+, but detected Java \d+ for ([\w\-_.]+\.java)\. Detected features: \[([^\]]*)\]"
)


def _parse_feature_list(text):
    # Accept either comma+space separated or comma separated
    text = (text or '').strip()
    if not text:
        return set()
    return {part.strip() for part in text.split(',') if part.strip()}


def parse_errors(stream, *, include_version_mismatch=False):
    """Return mapping: filename -> features.

    If include_version_mismatch is False:
      - parse only missing-feature lines and return only those missing features.

    If include_version_mismatch is True:
      - additionally parse version-mismatch lines and return the FULL detected feature set
        for that file.
      - if both kinds of entries exist for the same file, the version-mismatch feature set wins.
    """

    missing = defaultdict(set)

    for line in stream:
        m = FEATURE_LINE_RE.search(line)
        if m:
            feat, fname = m.group(1), m.group(2)
            missing[fname].add(feat)
            continue

        if include_version_mismatch:
            m2 = VERSION_MISMATCH_RE.search(line)
            if m2:
                fname = m2.group(1)
                feats = _parse_feature_list(m2.group(2))
                # For version mismatch, treat the detected feature list as authoritative
                missing[fname] = set(feats)

    return missing


def find_files(root, filename):
    matches = []
    for dirpath, dirs, files in os.walk(root):
        # do not descend into build output directories named 'target'
        dirs[:] = [d for d in dirs if d != 'target']
        # also skip any path that is already inside a 'target' directory
        if 'target' in dirpath.split(os.sep):
            continue
        if filename in files:
            matches.append(os.path.join(dirpath, filename))
    return matches


def show_snippet(path, max_lines=60):
    try:
        with open(path, 'r', encoding='utf-8') as f:
            lines = f.readlines()
    except Exception as e:
        print(f"  (error reading file: {e})")
        return []
    for i, line in enumerate(lines[:max_lines]):
        print(f"{i+1:3}: {line.rstrip()}")
    if len(lines) > max_lines:
        print(f"... ({len(lines)-max_lines} more lines)")
    return lines


def update_feature_line(lines, comment_key, to_add, *, mode="merge"):
    """Update a feature comment line.

    mode:
      - "merge": union with existing values (previous behavior)
      - "replace": overwrite existing values
    """

    if mode not in ("merge", "replace"):
        raise ValueError(f"Unknown mode: {mode}")

    # look for a line like: // Required Features: A, B, C
    pat = re.compile(r"(\s*//\s*" + re.escape(comment_key) + r"\s*:\s*)(.*)$")
    for i, line in enumerate(lines):
        m = pat.match(line)
        if m:
            prefix = m.group(1)
            existing = m.group(2).strip()
            existing_set = _parse_feature_list(existing)

            if mode == "replace":
                new_set = set(to_add)
            else:
                new_set = existing_set.union(to_add)

            new_line = prefix + ', '.join(sorted(new_set)) + "\n"
            lines[i] = new_line
            return lines, True

    # not found -> try to insert after top comment block
    insert_idx = 0
    if lines and lines[0].lstrip().startswith('//'):
        # find end of top contiguous comment block
        for i, line in enumerate(lines):
            if not line.lstrip().startswith('//'):
                insert_idx = i
                break
        else:
            insert_idx = len(lines)
    # insert new comment line
    new_line = f"// {comment_key}: {', '.join(sorted(to_add))}\n"
    lines.insert(insert_idx, new_line)
    return lines, True


def backup_file(path):
    bak = path + '.bak'
    shutil.copy2(path, bak)
    print(f"  backup created: {bak}")


def prompt_choice(fname, features, filepaths, *, mode, auto_choice=None):
    print('\n' + '='*72)
    print(f"File: {fname}")
    if not filepaths:
        print("  No matching files found in repo.")
        return None
    if len(filepaths) > 1:
        print("Multiple matches found:")
        for idx, p in enumerate(filepaths):
            print(f"  [{idx}] {p}")
        sel = input("Select index to open (or 's' to skip): ").strip()
        if sel.lower() == 's':
            return None
        try:
            idx = int(sel)
            path = filepaths[idx]
        except Exception:
            print("Invalid selection, skipping.")
            return None
    else:
        path = filepaths[0]
    print(f"Path: {path}\n")
    show_snippet(path)

    print('\nParsed features: ' + ', '.join(sorted(features)))
    if auto_choice:
        choice = auto_choice
        print(f"Auto-choice: {choice}")
    else:
        if mode == "replace":
            choice = input("Replace (r)equired features, (s)kip? [r/s]: ").strip().lower()
        else:
            choice = input("Add to (r)equired, (s)kip? [r/s]: ").strip().lower()

    if choice != 'r':
        print("Skipping.")
        return None

    backup_file(path)
    with open(path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    lines, _ = update_feature_line(lines, 'Required Features', features, mode=mode)
    with open(path, 'w', encoding='utf-8') as f:
        f.writelines(lines)
    print(f"Updated {path}")
    return path


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('errorfile', nargs='?', help='Path to error output file (default: stdin)')
    parser.add_argument('--root', default='.', help='Repository root to search (default: current dir)')
    parser.add_argument('--yes', action='store_true', help='Apply changes without prompting')
    parser.add_argument(
        '--replace',
        action='store_true',
        help='Replace the Required Features list instead of merging (can remove existing features)'
    )
    parser.add_argument(
        '--include-version-mismatch',
        action='store_true',
        help='Also parse version-mismatch lines that contain a full detected feature set'
    )
    args = parser.parse_args()

    mode = "replace" if args.replace else "merge"

    if args.errorfile:
        with open(args.errorfile, 'r', encoding='utf-8') as f:
            missing = parse_errors(f, include_version_mismatch=args.include_version_mismatch)
    else:
        missing = parse_errors(sys.stdin, include_version_mismatch=args.include_version_mismatch)

    if not missing:
        print('No applicable errors found in input.')
        return

    print(f"Found {len(missing)} files with applicable errors.")
    for fname, feats in sorted(missing.items()):
        matches = find_files(args.root, fname)
        if args.yes:
            if not matches:
                print(f"No file found for {fname}, skipping.")
                continue
            path = matches[0]
            backup_file(path)
            with open(path, 'r', encoding='utf-8') as f:
                lines = f.readlines()
            lines, _ = update_feature_line(lines, 'Required Features', feats, mode=mode)
            with open(path, 'w', encoding='utf-8') as f:
                f.writelines(lines)
            print(f"Updated {path} (required, mode={mode})")
        else:
            prompt_choice(fname, feats, matches, mode=mode)


if __name__ == '__main__':
    main()