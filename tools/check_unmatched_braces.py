#!/usr/bin/env python3
"""check_unmatched_braces.py

Tiny checker for unmatched or mis-ordered curly braces.

What it checks
- Every file is scanned left-to-right.
- Each '{' increments a counter.
- Each '}' decrements; if it would go below 0, that's a mis-ordered close (a '}' before a matching '{').
- If the final counter is not 0, there are unmatched '{'.

This is intentionally simple and does NOT try to ignore braces in strings/comments.

Usage:
  python3 tools/check_unmatched_braces.py --root .
  python3 tools/check_unmatched_braces.py path/to/file1 path/to/file2

Common options:
  --glob "**/*.java"   (default: **/*)
  --exclude target,.git,node_modules
  --fail               exit non-zero if any problems are found
"""

from __future__ import annotations

import argparse
import fnmatch
import os
import sys
from dataclasses import dataclass


@dataclass
class BraceIssue:
    path: str
    kind: str  # 'premature_close' | 'unclosed_open'
    position: int
    line: int
    col: int
    detail: str


def iter_files(root: str, pattern: str, excludes: set[str]):
    for dirpath, dirs, files in os.walk(root):
        # don't descend into excluded directories
        dirs[:] = [d for d in dirs if d not in excludes]
        parts = dirpath.split(os.sep)
        if any(p in excludes for p in parts):
            continue
        for name in files:
            rel = os.path.relpath(os.path.join(dirpath, name), root)
            if fnmatch.fnmatch(rel, pattern) or fnmatch.fnmatch(name, pattern):
                yield os.path.join(dirpath, name)


def line_col_from_index(text: str, idx: int) -> tuple[int, int]:
    # 1-based line/col
    line = text.count('\n', 0, idx) + 1
    last_nl = text.rfind('\n', 0, idx)
    col = idx - (last_nl + 1) + 1
    return line, col


def check_file(path: str) -> list[BraceIssue]:
    issues: list[BraceIssue] = []
    try:
        with open(path, 'r', encoding='utf-8', errors='ignore') as f:
            text = f.read()
    except Exception as e:
        return [BraceIssue(path, 'read_error', 0, 0, 0, f"Failed to read: {e}")]

    balance = 0
    for i, ch in enumerate(text):
        if ch == '{':
            balance += 1
        elif ch == '}':
            if balance == 0:
                line, col = line_col_from_index(text, i)
                issues.append(
                    BraceIssue(
                        path=path,
                        kind='premature_close',
                        position=i,
                        line=line,
                        col=col,
                        detail="Found '}' before any matching '{'",
                    )
                )
            else:
                balance -= 1

    if balance != 0:
        # report at EOF; simple and clear
        line, col = line_col_from_index(text, len(text))
        issues.append(
            BraceIssue(
                path=path,
                kind='unclosed_open',
                position=len(text),
                line=line,
                col=col,
                detail=f"Unclosed '{{' braces remaining: {balance}",
            )
        )

    return issues


def main(argv: list[str]) -> int:
    p = argparse.ArgumentParser()
    p.add_argument('paths', nargs='*', help='Files to check. If omitted, scans --root with --glob.')
    p.add_argument('--root', default='.', help='Root directory to scan when no explicit paths given (default: .)')
    p.add_argument('--glob', default='**/*', help='Glob pattern for files under --root (default: **/*)')
    p.add_argument('--exclude', default='target,.git,node_modules,.idea,.mvn,dist',
                   help='Comma-separated directory names to skip')
    p.add_argument('--fail', action='store_true', help='Exit non-zero if any issues are found')
    args = p.parse_args(argv)

    excludes = {x.strip() for x in args.exclude.split(',') if x.strip()}

    files: list[str]
    if args.paths:
        files = args.paths
    else:
        root = os.path.abspath(args.root)
        files = list(iter_files(root, args.glob, excludes))

    any_issues = False
    for path in files:
        # skip non-regular files
        if not os.path.isfile(path):
            continue
        issues = check_file(path)
        if issues:
            any_issues = True
            for iss in issues:
                if iss.kind == 'read_error':
                    print(f"{iss.path}: ERROR: {iss.detail}")
                else:
                    print(f"{iss.path}:{iss.line}:{iss.col}: {iss.kind}: {iss.detail}")

    if args.fail:
        return 1 if any_issues else 0
    return 0


if __name__ == '__main__':
    raise SystemExit(main(sys.argv[1:]))