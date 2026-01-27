#!/usr/bin/env python3
"""
Find and optionally remove empty Java files.

By default the script runs in dry-run mode and prints files that are empty (0 bytes).
Use --strip-comments to treat files that contain only comments/whitespace as empty.
Use --apply to actually remove files, or --move-to DIR to move them into a backup directory.

Usage examples:
  # list zero-byte .java files
  python3 tools/remove_empty_java_files.py --root .

  # list files that are empty after stripping comments/whitespace
  python3 tools/remove_empty_java_files.py --root . --strip-comments

  # actually delete zero-byte files
  python3 tools/remove_empty_java_files.py --root . --apply

  # move comment-only files into .empty_backup
  python3 tools/remove_empty_java_files.py --root . --strip-comments --apply --move-to .empty_backup
"""

import argparse
import os
import re
import shutil
import sys

BLOCK_COMMENT_RE = re.compile(r"/\*.*?\*/", re.DOTALL)
LINE_COMMENT_RE = re.compile(r"//.*?$", re.MULTILINE)


def is_effectively_empty(path: str) -> bool:
    try:
        with open(path, 'r', encoding='utf-8', errors='ignore') as f:
            text = f.read()
    except Exception:
        return False
    # remove block comments and line comments
    without_block = BLOCK_COMMENT_RE.sub('', text)
    without_line = LINE_COMMENT_RE.sub('', without_block)
    # if nothing but whitespace remains, it's effectively empty
    return without_line.strip() == ''


def walk_java_files(root: str, exclude_names):
    for dirpath, dirs, files in os.walk(root):
        # prevent descending into excluded directories
        dirs[:] = [d for d in dirs if d not in exclude_names]
        # skip any path that is already inside an excluded dir
        parts = dirpath.split(os.sep)
        if any(part in exclude_names for part in parts):
            continue
        for f in files:
            if f.endswith('.java'):
                yield os.path.join(dirpath, f)


def main():
    p = argparse.ArgumentParser()
    p.add_argument('--root', default='.', help='Repository root to scan')
    p.add_argument('--exclude', action='append', default=['target'], help='Directory names to exclude from walking (can repeat)')
    p.add_argument('--strip-comments', action='store_true', help='Treat files that contain only comments/whitespace as empty')
    p.add_argument('--apply', action='store_true', help='Actually remove or move the found files; otherwise dry-run')
    p.add_argument('--move-to', default=None, help='When used with --apply, move files to this directory instead of deleting')
    p.add_argument('--verbose', action='store_true')
    args = p.parse_args()

    root = os.path.abspath(args.root)
    exclude = set(args.exclude)
    if args.verbose:
        print(f"Scanning {root}, excluding directories: {sorted(exclude)}")

    candidates = []
    for path in walk_java_files(root, exclude):
        try:
            size = os.path.getsize(path)
        except Exception:
            continue
        if size == 0:
            candidates.append((path, 'zero-bytes'))
            continue
        if args.strip_comments:
            if is_effectively_empty(path):
                candidates.append((path, 'comments-only'))

    if not candidates:
        print('No empty .java files found (with current options).')
        return

    print(f"Found {len(candidates)} empty .java files:")
    for path, reason in candidates:
        print(f"  [{reason}] {path}")

    if not args.apply:
        print('\nDry-run mode; no files were changed. Use --apply to remove/move them.')
        return

    # Apply changes
    if args.move_to:
        backup_root = os.path.abspath(args.move_to)
        os.makedirs(backup_root, exist_ok=True)

    for path, reason in candidates:
        try:
            if args.move_to:
                rel = os.path.relpath(path, root)
                dest = os.path.join(backup_root, rel)
                os.makedirs(os.path.dirname(dest), exist_ok=True)
                shutil.move(path, dest)
                print(f"Moved {path} -> {dest}")
            else:
                os.remove(path)
                print(f"Deleted {path}")
        except Exception as e:
            print(f"Failed to remove {path}: {e}")

    print('\nDone.')

if __name__ == '__main__':
    main()