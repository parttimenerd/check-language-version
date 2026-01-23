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

Building
--------

```shell
mvn package
```

Or with GraalVM native image support:

```shell
./build-native-image.sh
```

License
-------
MIT