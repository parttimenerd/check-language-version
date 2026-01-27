#!/usr/bin/env python3
"""
Remove duplicate Java test files (ignoring comments).

Usage:
  # dry-run: list duplicates without changing files
  python3 tools/remove_duplicate_tests.py --root .

  # apply: move duplicates to backup directory
  python3 tools/remove_duplicate_tests.py --root . --apply --backup-dir .duplicates_removed

Options:
  --root PATH        Repository root to scan (default: .)
  --exclude DIR      Directory name to exclude while walking (can be repeated; default: target)
  --backup-dir DIR   Where to move duplicates when --apply is used (default: .duplicates_removed)
  --delete           Permanently delete duplicates instead of moving them
  --apply            Apply changes (move or delete). Without --apply script only reports.
  --min-size N       Ignore files smaller than N bytes (default: 0)
  --verbose          Verbose logging

Notes:
- The script normalizes files by removing Java block comments (/* */) and line comments (// ...)
  and blank lines before computing a hash. This is a heuristic and may change content inside
  string literals that contain comment-like sequences. Use with care and review the dry-run
  output before applying.
"""

import argparse
import hashlib
import os
import re
import shutil
import sys
import datetime
from collections import defaultdict

BLOCK_COMMENT_RE = re.compile(r"/\*.*?\*/", re.DOTALL)
LINE_COMMENT_RE = re.compile(r"//.*?$", re.MULTILINE)


def normalize_java_source(text: str) -> str:
    """Remove block and line comments and blank lines, normalize whitespace."""
    # remove block comments
    without_block = BLOCK_COMMENT_RE.sub('', text)
    # remove line comments
    without_line = LINE_COMMENT_RE.sub('', without_block)
    # split lines, strip, and keep non-empty
    lines = [re.sub(r'\s+', ' ', ln).strip() for ln in without_line.splitlines()]
    nonempty = [ln for ln in lines if ln]
    return '\n'.join(nonempty)


def file_hash(path: str) -> str:
    with open(path, 'r', encoding='utf-8', errors='ignore') as f:
        text = f.read()
    norm = normalize_java_source(text)
    h = hashlib.sha1(norm.encode('utf-8')).hexdigest()
    return h


def walk_java_files(root: str, exclude_names):
    for dirpath, dirs, files in os.walk(root):
        # filter out excluded dir names from descent
        dirs[:] = [d for d in dirs if d not in exclude_names]
        # also skip any path already inside an excluded dir
        parts = dirpath.split(os.sep)
        if any(part in exclude_names for part in parts):
            continue
        for f in files:
            if f.endswith('.java'):
                yield os.path.join(dirpath, f)


def choose_keep_file(candidates):
    """Choose which file to keep among duplicates.
    Prefer files that live under 'src/' (if present), otherwise the one with shortest path.
    """
    def score(p):
        score0 = 0 if os.path.sep + 'src' + os.path.sep in p or p.startswith('src' + os.path.sep) else 1
        depth = p.count(os.path.sep)
        return (score0, depth, p)
    return sorted(candidates, key=score)[0]


def main():
    p = argparse.ArgumentParser()
    p.add_argument('--root', default='.', help='Repository root to scan')
    p.add_argument('--exclude', action='append', default=['target'], help="Directory names to exclude from walking; can repeat")
    p.add_argument('--backup-dir', default='.duplicates_removed', help='Where to move duplicates when --apply')
    p.add_argument('--apply', action='store_true', help='Actually move/delete duplicates; otherwise just report')
    p.add_argument('--delete', action='store_true', help='Permanently delete duplicates when used with --apply')
    p.add_argument('--min-size', type=int, default=0, help='Ignore files smaller than this (bytes)')
    p.add_argument('--verbose', action='store_true')
    args = p.parse_args()

    root = os.path.abspath(args.root)
    exclude = set(args.exclude)

    print(f"Scanning for .java files under {root} (excluding dirs: {sorted(exclude)})")

    hashes = defaultdict(list)
    count_files = 0
    for path in walk_java_files(root, exclude):
        try:
            size = os.path.getsize(path)
        except OSError:
            continue
        if size < args.min_size:
            continue
        try:
            h = file_hash(path)
        except Exception as e:
            if args.verbose:
                print(f"Skipping {path}: error reading ({e})")
            continue
        hashes[h].append(path)
        count_files += 1

    print(f"Found {count_files} .java files (after size filter).")

    duplicates = {h: paths for h, paths in hashes.items() if len(paths) > 1}
    if not duplicates:
        print('No duplicates found.')
        return

    print(f"Found {len(duplicates)} groups of duplicates.")

    for h, paths in duplicates.items():
        print('\n=== Duplicate group ===')
        for p in paths:
            print('  ' + p)
        keep = choose_keep_file(paths)
        print(f"Keeping: {keep}")
        to_remove = [p for p in paths if p != keep]
        if not args.apply:
            print(f"Would remove {len(to_remove)} file(s):")
            for p in to_remove:
                print('   ' + p)
            continue
        # apply changes
        timestamp = datetime.datetime.now().strftime('%Y%m%d_%H%M%S')
        backup_root = os.path.abspath(args.backup_dir)
        if not args.delete:
            # move to backup dir preserving relative path
            for p in to_remove:
                rel = os.path.relpath(p, root)
                dest = os.path.join(backup_root, timestamp, rel)
                os.makedirs(os.path.dirname(dest), exist_ok=True)
                shutil.move(p, dest)
                print(f"Moved {p} -> {dest}")
        else:
            for p in to_remove:
                try:
                    os.remove(p)
                    print(f"Deleted {p}")
                except Exception as e:
                    print(f"Failed to delete {p}: {e}")

    print('\nDone.')

if __name__ == '__main__':
    main()