# Object Sizes

Measure object graph sizes for a curated set of tiny Java programs and emit the results as JSON.

This project is meant as a data generator: it runs each example program, finds a `value` root object, and uses JOL (Java Object Layout) to compute:

- a detailed object graph layout (based on `GraphLayout#toPrintable()`)
- a summarized footprint (based on `GraphLayout#toFootprint()`)

The output is a JSON array of results (one entry per program).

## Requirements

- Java (recent enough to run JOL; some setups may require additional JVM flags, see below)
- Maven

> Note: If you run with compact object headers (`-XX:+UseCompactObjectHeaders`), you typically need a very recent JDK.

## Build

```shell
mvn package
```

This produces `target/object-sizes.jar`.

## Run

Build first:

```shell
mvn package
```

You can either invoke the jar directly, or use the convenience wrapper script `bin/object-sizes`.

### Using `bin/object-sizes` (recommended)

Basic run (all programs, overwrite `object-sizes.json`):

```shell
bin/object-sizes
```

Write to a custom file:

```shell
bin/object-sizes --output results.json
```

Run a single program:

```shell
bin/object-sizes --class me.bechberger.sizes.programs.strings.Latin1Len9
```

Append results to an existing JSON file:

```shell
bin/object-sizes --output results.json --append
```

If you need to pass extra JVM flags (see below), set them via `JAVA_TOOL_OPTIONS`, e.g.:

```shell
JAVA_TOOL_OPTIONS='-Djdk.attach.allowAttachSelf -XX:+EnableDynamicAgentLoading' bin/object-sizes
```

### Using the jar directly

Basic run (all programs, overwrite `object-sizes.json`):

```shell
java -Djdk.attach.allowAttachSelf -XX:+EnableDynamicAgentLoading -jar target/object-sizes.jar
```

Write to a custom file:

```shell
java -Djdk.attach.allowAttachSelf -XX:+EnableDynamicAgentLoading \
  -jar target/object-sizes.jar \
  --output results.json
```

Run a single program:

```shell
java -Djdk.attach.allowAttachSelf -XX:+EnableDynamicAgentLoading \
  -jar target/object-sizes.jar \
  --class me.bechberger.sizes.programs.strings.Latin1Len9
```

Append results to an existing JSON file (merging by `class`):

```shell
java -Djdk.attach.allowAttachSelf -XX:+EnableDynamicAgentLoading \
  -jar target/object-sizes.jar \
  --output results.json \
  --append
```

### Compact headers mode

The CLI records the expected `UseCompactObjectHeaders` setting in JSON via:

```shell
--compact-headers true|false
```

This flag **does not** toggle the VM option by itself. To actually run with/without compact headers, invoke the JVM with:

- `-XX:+UseCompactObjectHeaders`
- `-XX:-UseCompactObjectHeaders`

A typical workflow is to run twice (once for each VM setting) and append/merge results.

## Program format

A “program” is a tiny Java class under `src/main/java/me/bechberger/sizes/programs/**`.

Each program must define **either**:

- a non-static field named `value`, **or**
- a non-static zero-arg method named `value()`

The tool instantiates the program and measures the object graph reachable from `value`.

Example:

```java
package me.bechberger.sizes.programs;

class StringSize {
    String value = "abcdefg";
}
```

The list of runnable programs is generated into `src/main/resources/me/bechberger/sizes/programs.index`.

## Output JSON format

The tool writes a JSON array. Each entry looks like:

- `class`: fully-qualified program class name
- `code`: best-effort source snippet (without the `package` line)
- `sanitizedCode`: like `code`, but with comments removed and the top-level class name normalized to `Test` (preserves line breaks)
- `rating`: currently the number of parsed graph rows (best-effort)
- `timestamp`: ISO timestamp when the measurement was taken
- `layout`: an array of layout runs (typically one entry per invocation)

Each `layout` entry contains:

- `totalSize`: JOL’s `GraphLayout#totalSize()`
- `layout`: parsed rows from `GraphLayout#toPrintable()`
- `footprint`: parsed rows from `GraphLayout#toFootprint()`
- `UseCompactObjectHeaders`: the `--compact-headers` value you passed

### Parsed layout rows

A parsed layout row has:

- `size`: object size (bytes)
- `type`: object type
- `path`: reference path (when available)
- `value`: JOL’s value column (best-effort)

Note: JOL sometimes prints placeholder rows like `(something else)`. The parser tries to keep those intact.

### Footprint rows

A footprint row has:

- `count`, `avg`, `sum`
- `description`: type description (inner classes are normalized to their simple inner name)

## Notes on JOL / attach warnings

Depending on your OS/JDK, JOL may try to attach to the current JVM to improve accuracy.
If you see attach warnings, the flags below often help:

- `-Djdk.attach.allowAttachSelf`
- `-XX:+EnableDynamicAgentLoading`

Some environments (notably record layouts on some JDK builds) may require:

- `-Djol.magicFieldOffset=true`

Use this only if you hit errors like “Cannot get the field offset”.

## Support, Feedback, Contributing

This project is open to feature requests/suggestions, bug reports etc.
via GitHub issues.
Contribution and feedback are encouraged and always welcome.

## License

MIT, Copyright 2026 SAP SE or an SAP affiliate company, Johannes Bechberger and contributors