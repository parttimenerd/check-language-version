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

Game idea
---------
I have in the tests a lot of tiny Java files with different language features
and different Java versions.
Maybe create a game where you have to guess the Java version
based on the code snippet?

create a self-contained python script in a game folder
that generates a quiz from these files (embed all files in the JS
with the language features that define the version and the minimum version required
as the answer).
The user is shown a code snippet and has to select the correct Java version
from multiple choices (auto-generate four random wrong answers and add correct
answer).
The game keeps track of score (sum of absolute differences to actual version) and time.
Use now web framework in the backend and pure vanilla JS in the frontend.
keep it simple.

the python script should generate a simple HTML file with embedded JS and CSS.