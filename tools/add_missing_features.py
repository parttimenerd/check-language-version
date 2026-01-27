#!/usr/bin/env python3
"""
Simple helper to parse test-suite error output and interactively add missing features
to test specification files. Usage:

  cat errors.txt | python3 tools/add_missing_features.py

Or:
  python3 tools/add_missing_features.py errors.txt

It looks for lines like:
  Detected feature 'SWITCH_EXPRESSIONS' is not listed in required or optional features for Tiny_SwitchNull_Java21.java.

For each referenced .java file it searches the repo for a matching filename, shows a snippet,
then asks whether to add the missing feature(s) to Required or Optional features.
The script creates a .bak backup before modifying files.
"""

import argparse
import os
import re
import shutil
import sys
from collections import defaultdict

FEATURE_LINE_RE = re.compile(r"Detected feature '([^']+)' is not listed .* for ([\w\-_.]+\.java)\.")


def parse_errors(stream):
    missing = defaultdict(set)
    for line in stream:
        m = FEATURE_LINE_RE.search(line)
        if m:
            feat, fname = m.group(1), m.group(2)
            missing[fname].add(feat)
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


def update_feature_line(lines, comment_key, to_add):
    # look for a line like: // Required Features: A, B, C
    pat = re.compile(r"(\s*//\s*" + re.escape(comment_key) + r"\s*:\s*)(.*)$")
    for i, line in enumerate(lines):
        m = pat.match(line)
        if m:
            prefix = m.group(1)
            existing = m.group(2).strip()
            existing_set = {s.strip() for s in existing.split(',') if s.strip()} if existing else set()
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


def prompt_choice(fname, features, filepaths, auto_choice=None):
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
    lines = show_snippet(path)

    print('\nDetected missing features: ' + ', '.join(sorted(features)))
    if auto_choice:
        choice = auto_choice
        print(f"Auto-choice: {choice}")
    else:
        choice = input("Add to (r)equired, (s)kip? [r/s]: ").strip().lower()

    if choice != 'r':
        print("Skipping.")
        return None
    # perform update
    backup_file(path)
    with open(path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    if choice == 'r':
        lines, _ = update_feature_line(lines, 'Required Features', features)
    with open(path, 'w', encoding='utf-8') as f:
        f.writelines(lines)
    print(f"Updated {path}")
    return path


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('errorfile', nargs='?', help='Path to error output file (default: stdin)')
    parser.add_argument('--root', default='.', help='Repository root to search (default: current dir)')
    parser.add_argument('--yes', action='store_true', help='Automatically add missing features to Required Features')
    args = parser.parse_args()

    if args.errorfile:
        with open(args.errorfile, 'r', encoding='utf-8') as f:
            missing = parse_errors(f)
    else:
        missing = parse_errors(sys.stdin)

    if not missing:
        print('No missing-feature errors found in input.')
        return

    print(f"Found {len(missing)} files with missing-feature errors.")
    for fname, feats in sorted(missing.items()):
        matches = find_files(args.root, fname)
        if args.yes:
            # non-interactive: add to required
            if not matches:
                print(f"No file found for {fname}, skipping.")
                continue
            path = matches[0]
            backup_file(path)
            with open(path, 'r', encoding='utf-8') as f:
                lines = f.readlines()
            lines, _ = update_feature_line(lines, 'Required Features', feats)
            with open(path, 'w', encoding='utf-8') as f:
                f.writelines(lines)
            print(f"Auto-updated {path} (required)")
        else:
            prompt_choice(fname, feats, matches)

if __name__ == '__main__':
    main()