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
JavaParser fails to parse some valid Java files.
It's all just an approximation.
The matching of library features is limited, as we can only
do type based checking, as we're not doing any semantic analysis.

Building
--------

```shell
mvn package
```

Or with GraalVM native image support:

```shell
./build-native-image.sh
```

Testing
-------

```shell
mvn test
```

To test also test that the test Java files compile, run:

```shell
mvn test -Dtest=FeatureDetectionTest -Dtest.compilation=true
```

Visualization
-------------

```bash
python3 visualize.py output.json bla.json --output-dir test_split_bars --open
```

License
-------
Apache License 2.0