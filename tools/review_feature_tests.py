#!/usr/bin/env python3
"""review_feature_tests.py

Self-contained interactive reviewer for feature_test Java files.

Behavior
- Iterates over Java files under src/test/resources/feature_tests (or a provided list/glob).
- For each file:
  - Extracts the feature list from header comments (Required/Optional Features).
  - Pretty-prints the file with Java syntax highlighting (Pygments) to the terminal.
  - Shows the parsed feature list.
  - Waits for a key:
      * Enter: mark as correct and proceed
      * anything else: open the file in vim for editing (optionally at a line number)

Self-contained dependency handling
- Uses a local venv at .venv-review-feature-tests/
- If Pygments is missing, it will create the venv and install Pygments automatically.

Usage examples
  python3 tools/review_feature_tests.py
  python3 tools/review_feature_tests.py --glob 'src/test/resources/feature_tests/**/*.java'
  python3 tools/review_feature_tests.py --file src/test/resources/feature_tests/Java5_Generics.java

Tips
- Use --start-at PATTERN to resume (matches substring in path).
- Use --vim-line 1-based line number to open directly at a line.
"""

from __future__ import annotations

import argparse
import fnmatch
import os
import re
import subprocess
import sys
from pathlib import Path


VENV_DIR = Path('.venv-review-feature-tests')


def _run(cmd: list[str], **kwargs) -> subprocess.CompletedProcess:
    return subprocess.run(cmd, check=False, text=True, **kwargs)


def ensure_pygments():
    """Import pygments; if missing, bootstrap a local venv and install it, then re-exec."""
    try:
        import pygments  # noqa: F401
        return
    except Exception:
        pass

    # If we're already running inside our venv but still can't import, bail.
    if os.environ.get('REVIEW_FEATURE_TESTS_BOOTSTRAPPED') == '1':
        print('Failed to import pygments even after bootstrapping.', file=sys.stderr)
        raise

    python = sys.executable
    venv_python = VENV_DIR / 'bin' / 'python'

    if not venv_python.exists():
        print(f"Creating venv at {VENV_DIR}...")
        _run([python, '-m', 'venv', str(VENV_DIR)])

    print('Installing pygments into venv...')
    _run([str(venv_python), '-m', 'pip', 'install', '--upgrade', 'pip'], stdout=subprocess.DEVNULL)
    res = _run([str(venv_python), '-m', 'pip', 'install', 'pygments>=2.17'])
    if res.returncode != 0:
        print(res.stdout)
        print(res.stderr, file=sys.stderr)
        raise SystemExit(res.returncode)

    # Re-exec ourselves using venv python
    env = dict(os.environ)
    env['REVIEW_FEATURE_TESTS_BOOTSTRAPPED'] = '1'
    print('Re-launching in venv...')
    os.execve(str(venv_python), [str(venv_python)] + sys.argv, env)


def discover_files(root: Path, pattern: str, exclude_dirs: set[str]) -> list[Path]:
    files: list[Path] = []
    cwd = Path.cwd()
    for dirpath, dirs, filenames in os.walk(root):
        dirs[:] = [d for d in dirs if d not in exclude_dirs]
        dp = Path(dirpath)
        if any(part in exclude_dirs for part in dp.parts):
            continue
        for name in filenames:
            p = dp / name
            # try several representations to match against the provided pattern:
            # - path relative to the provided root (intended behavior)
            # - filename only
            # - path relative to the current working directory (useful when pattern contains a repo-root prefix)
            # - absolute path
            try:
                rel_root = str(p.relative_to(root))
            except Exception:
                rel_root = str(p)
            try:
                rel_cwd = str(p.relative_to(cwd))
            except Exception:
                rel_cwd = str(p)

            if (
                fnmatch.fnmatch(rel_root, pattern)
                or fnmatch.fnmatch(name, pattern)
                or fnmatch.fnmatch(rel_cwd, pattern)
                or fnmatch.fnmatch(str(p), pattern)
            ):
                files.append(p)
    return sorted(files)


REQ_RE = re.compile(r'^\s*//\s*Required Features:\s*(.*)\s*$')
OPT_RE = re.compile(r'^\s*//\s*Optional Features:\s*(.*)\s*$')
EXP_RE = re.compile(r'^\s*//\s*Expected Version:\s*([+-]?\d+)\b')


def parse_header(text: str):
    required: list[str] = []
    optional: list[str] = []
    expected = None

    for line in text.splitlines()[:60]:
        m = EXP_RE.match(line)
        if m:
            expected = int(m.group(1))
        m = REQ_RE.match(line)
        if m:
            required = [x.strip().strip(',') for x in m.group(1).split() if x.strip().strip(',')]
        m = OPT_RE.match(line)
        if m:
            optional = [x.strip().strip(',') for x in m.group(1).split() if x.strip().strip(',')]

    return expected, required, optional


def highlight_java(code: str, theme: str) -> str:
    from pygments import highlight
    from pygments.formatters import Terminal256Formatter
    from pygments.lexers import JavaLexer

    return highlight(code, JavaLexer(), Terminal256Formatter(style=theme))


def clear_screen():
    # portable-ish
    sys.stdout.write('\033[2J\033[H')
    sys.stdout.flush()


def read_single_key() -> str:
    """Return a single keypress; Enter returns '\n'."""
    try:
        import termios
        import tty

        fd = sys.stdin.fileno()
        old = termios.tcgetattr(fd)
        try:
            tty.setraw(fd)
            ch = sys.stdin.read(1)
            return ch
        finally:
            termios.tcsetattr(fd, termios.TCSADRAIN, old)
    except Exception:
        # fallback: line-based
        return sys.stdin.readline()[:1] or '\n'


def open_in_vim(path: Path, line: int | None):
    editor = os.environ.get('EDITOR', 'vim')
    cmd = [editor]
    if line and editor.endswith('vim'):
        cmd += [f'+{line}']
    cmd += [str(path)]
    _run(cmd)


def main(argv: list[str]) -> int:
    ensure_pygments()

    ap = argparse.ArgumentParser()
    ap.add_argument('--root', default='.', help='Repo root (default: .)')
    ap.add_argument('--glob', default='**/*.java', help="Glob relative to --root (default: '**/*.java')")
    ap.add_argument('--exclude', default='target,.git,.idea,.mvn,game/dist', help='Comma-separated dir names to skip')
    ap.add_argument('--file', action='append', default=[], help='Specific file(s) to review (repeatable)')
    ap.add_argument('--start-at', default=None, help='Resume at first file whose path contains this substring')
    ap.add_argument('--vim-line', type=int, default=None, help='1-based line number to jump to in vim when editing')
    ap.add_argument('--theme', default='monokai', help='Pygments terminal theme (default: monokai, high-contrast). Examples: monokai, native, vim, friendly')
    args = ap.parse_args(argv)

    root = Path(args.root).resolve()
    exclude_dirs = {x.strip() for x in args.exclude.split(',') if x.strip()}

    if args.file:
        files = [Path(f).resolve() for f in args.file]
    else:
        files = discover_files(root, args.glob, exclude_dirs)

    if not files:
        print('No files found.', file=sys.stderr)
        return 2

    # apply start-at
    if args.start_at:
        idx = 0
        for i, p in enumerate(files):
            if args.start_at in str(p):
                idx = i
                break
        files = files[idx:]

    for i, path in enumerate(files, start=1):
        try:
            text = path.read_text(encoding='utf-8', errors='ignore')
        except Exception as e:
            print(f"Failed to read {path}: {e}")
            continue

        expected, required, optional = parse_header(text)

        clear_screen()
        rel = os.path.relpath(path, root)
        print(f"[{i}/{len(files)}] {rel}")
        if expected is not None:
            print(f"Expected Version: {expected}")
        print(f"Required Features ({len(required)}): {', '.join(required) if required else '(none)'}")
        print(f"Optional Features ({len(optional)}): {', '.join(optional) if optional else '(none)'}")
        print('-' * 80)
        print(highlight_java(text, args.theme))
        print('-' * 80)
        print("Enter = correct / any other key = edit in vim / q = quit")

        ch = read_single_key()
        if ch in ('q', 'Q'):
            return 0
        if ch == '\r' or ch == '\n':
            # accepted
            continue

        open_in_vim(path, args.vim_line)

    return 0


if __name__ == '__main__':
    raise SystemExit(main(sys.argv[1:]))