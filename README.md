Check-Language-Version
======================

A tiny program to check which Java language versions
the files in a directory are using.
This is truely approximate and ignores used standard library APIs,
it just focuses on syntax.

For this it uses the validators of [JavaParser](https://javaparser.org/),
which are good enough for a rough estimate.
For simplicity, our lovest version is Java 8, as there were too many
changes from Java 7 to Java 8.

Usage
-----
```bash
java -jar target/check-language-version.jar --help
Usage: check-language-version [-hjsvV] [<files>...] [COMMAND]
Check the minimum Java language version required for Java source files
      [<files>...]   Java source files or directories to check
  -h, --help         Show this help message and exit.
  -j, --json         Output results in JSON format
  -s, --summary      Show summary of all files
  -v, --verbose      Show verbose output
  -V, --version      Print version information and exit.
Commands:
  summary  Show a summary table of Java versions from JSON output files
```

Known Limitations
-----------------
JavaParser fails to parse some valid Java files:
- `yield` is not recognized as a keyword in switch expressions
  - but it this seems to really rarely used in practice (only five files in Quarkus seem to contain it for example)
- local enums are not supported

Building
--------

```shell
mvn package
```

Or with GraalVM native image support:

```shell
./build-native-image.sh
```

Visualization
-------------

```bash
python3 visualize.py output.json bla.json --output-dir test_split_bars --open
```

License
-------
Apache License 2.0

TODO
----
Maybe rewrite using internal Java compiler APIs for better accuracy. Or just maybe not.