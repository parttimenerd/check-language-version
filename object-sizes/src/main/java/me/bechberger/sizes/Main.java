package me.bechberger.sizes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.bechberger.minicli.MiniCli;
import me.bechberger.minicli.Spec;
import me.bechberger.minicli.annotations.Command;
import me.bechberger.minicli.annotations.Option;
import org.openjdk.jol.info.GraphLayout;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@Command(
        name = "object-sizes",
        description = "Measure object graph sizes for example programs and emit JSON",
        mixinStandardHelpOptions = true,
        version = "1.0-SNAPSHOT"
)
public class Main implements Runnable {

    Spec spec; // injected

    @Option(names = "--output", paramLabel = "FILE", defaultValue = "object-sizes.json",
            description = "Write results to FILE (default: ${DEFAULT-VALUE})")
    Path output;

    @Option(names = "--append", description = "Append to existing output file instead of overwriting")
    boolean append;

    @Option(names = "--class", paramLabel = "FQCN",
            description = "Run only a single program class (fully-qualified class name)")
    String onlyClass;

    @Option(names = "--compact-headers", paramLabel = "true|false",
            description = "Expected UseCompactObjectHeaders setting; for now only recorded in JSON")
    Boolean compactHeaders;

    public static void main(String[] args) {
        System.exit(MiniCli.run(new Main(), System.out, System.err, args));
    }

    @Override
    public void run() {
        try {
            var programs = loadProgramClassNames();
            if (onlyClass != null && !onlyClass.isBlank()) {
                programs = programs.stream().filter(onlyClass::equals).toList();
            }

            var results = new ArrayList<ProgramResults.ProgramResult>();
            for (String fqcn : programs) {
                results.add(runSingle(fqcn));
            }

            writeResults(results);
        } catch (Exception e) {
            spec.err().println("ERROR: " + e.getMessage());
            e.printStackTrace(spec.err());
            throw new RuntimeException(e);
        }
    }

    private List<String> loadProgramClassNames() throws IOException {
        String resource = "/me/bechberger/sizes/programs.index";
        try (InputStream in = Main.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IOException("Missing programs index resource: " + resource + " (run mvn package)");
            }
            String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return text.lines()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }
    }

    private ProgramResults.ProgramResult runSingle(String fqcn) throws Exception {
        Class<?> clazz = Class.forName(fqcn);

        String code = readSourceWithoutPackage(clazz).orElse(null);
        String sanitizedCode = code == null ? null : sanitizeSourceForJson(code);

        Object instance = instantiate(clazz);
        ValueAccessor accessor = ValueAccessor.forClass(clazz);
        Object value = accessor.getValue(instance);

        // IMPORTANT: pass the root as an element in an Object[] to avoid corner cases with self-referential arrays.
        GraphLayout gl = GraphLayout.parseInstance(value);
        String printable = null;
        try {
            printable = filterJolNoise(gl.toPrintable());
        } catch (StackOverflowError e) {
            // Best-effort: fails with cycles
        }
        String footprint = filterJolNoise(gl.toFootprint());
        Long totalSize = gl.totalSize();

        var parsedLayout = ProgramResults.parseGraphLayoutPrintable(printable);
        var parsedFootprint = ProgramResults.parseFootprint(footprint);
        Integer rating = parsedLayout == null ? null : parsedLayout.size();

        var layoutResult = new ProgramResults.LayoutResult(
                totalSize,
                parsedLayout,
                parsedFootprint,
                compactHeaders
        );

        return new ProgramResults.ProgramResult(
                fqcn,
                code,
                sanitizedCode,
                rating,
                Instant.now().toString(),
                List.of(layoutResult)
        );
    }

    private static Object instantiate(Class<?> clazz) throws Exception {
        var ctor = clazz.getDeclaredConstructor();
        ctor.setAccessible(true);
        return ctor.newInstance();
    }

    private Optional<String> readSourceWithoutPackage(Class<?> clazz) {
        // Best-effort: map class to its .java file under src/main/java.
        // This is meant for local runs; in a packaged jar sources may not exist.
        String rel = "src/main/java/" + clazz.getName().replace('.', '/') + ".java";
        Path p = Path.of(rel);
        if (!Files.exists(p)) return Optional.empty();
        try {
            String src = Files.readString(p);
            return Optional.of(src.lines()
                    .filter(l -> !l.trim().startsWith("package "))
                    .collect(Collectors.joining("\n"))
                    .trim());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private void writeResults(List<ProgramResults.ProgramResult> newResults) throws IOException {
        ObjectMapper om = new ObjectMapper();
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        om.enable(SerializationFeature.INDENT_OUTPUT);

        List<ProgramResults.ProgramResult> out;
        if (append && Files.exists(output)) {
            try {
                out = om.readValue(Files.readString(output), new TypeReference<>() {
                });
                out = new ArrayList<>(out);
            } catch (Exception e) {
                throw new IOException("Failed to read existing JSON array from " + output, e);
            }

            out = mergeByClass(out, newResults);
        } else {
            out = newResults;
        }

        Files.createDirectories(output.toAbsolutePath().getParent() == null ? Path.of(".") : output.toAbsolutePath().getParent());
        Files.writeString(output, om.writeValueAsString(out), StandardCharsets.UTF_8);
        spec.out().println("Wrote " + out.size() + " program result(s) to " + output);
    }

    private static List<ProgramResults.ProgramResult> mergeByClass(List<ProgramResults.ProgramResult> existing,
                                                                   List<ProgramResults.ProgramResult> incoming) {
        // Preserve existing order; merge layouts into the first matching class entry.
        var byClass = new ArrayList<>(existing);

        for (var in : incoming) {
            int idx = -1;
            for (int i = 0; i < byClass.size(); i++) {
                if (byClass.get(i).className().equals(in.className())) {
                    idx = i;
                    break;
                }
            }

            if (idx < 0) {
                byClass.add(in);
                continue;
            }

            var ex = byClass.get(idx);
            var mergedLayouts = new ArrayList<ProgramResults.LayoutResult>();
            if (ex.layout() != null) mergedLayouts.addAll(ex.layout());
            if (in.layout() != null) mergedLayouts.addAll(in.layout());

            // If code is missing on the existing entry but present in incoming, take it.
            String code = ex.code();
            if ((code == null || code.isBlank()) && in.code() != null && !in.code().isBlank()) {
                code = in.code();
            }
            String sanitizedCode = ex.sanitizedCode();
            if ((sanitizedCode == null || sanitizedCode.isBlank()) && in.sanitizedCode() != null && !in.sanitizedCode().isBlank()) {
                sanitizedCode = in.sanitizedCode();
            }
            Integer rating = ex.rating();
            if (rating == null && in.rating() != null) {
                rating = in.rating();
            }

            // Timestamp: keep existing (first-seen) for stability.
            byClass.set(idx, new ProgramResults.ProgramResult(ex.className(), code, sanitizedCode, rating, ex.timestamp(), mergedLayouts));
        }

        return byClass;
    }

    private static String sanitizeSourceForJson(String codeWithoutPackage) {
        // 1) Remove line comments.
        String noLineComments = codeWithoutPackage.replaceAll("(?m)//.*$", "");
        // 2) Remove block comments (best-effort, non-nested).
        String noComments = noLineComments.replaceAll("(?s)/\\*.*?\\*/", "");

        // 3) Remove @SuppressWarnings("removal") annotations (keep other suppressions intact).
        // Allow arbitrary whitespace/newlines inside parentheses.
        String scrubbed = noComments.replaceAll(
                "(?s)@SuppressWarnings\\s*\\(\\s*\\\"removal\\\"\\s*\\)\\s*",
                ""
        );

        // Determine top-level class name from the first 'class <Name>' occurrence.
        var m = java.util.regex.Pattern
                .compile("\\bclass\\s+([A-Za-z_$][A-Za-z0-9_$]*)\\b")
                .matcher(scrubbed);
        if (!m.find()) {
            return normalizeWhitespacePreserveNewlines(scrubbed);
        }
        String original = m.group(1);

        // 1) Rename constructor declarations explicitly: ^\s*Original\s*\(
        // (We include leading whitespace to avoid renaming method calls like foo.Original( ) ).
        String renamed = scrubbed.replaceAll(
                "(?m)^(\\s*)" + java.util.regex.Pattern.quote(original) + "\\s*\\(",
                "$1Test("
        );

        // 2) Rename remaining occurrences of the top-level class identifier.
        renamed = renamed.replaceAll("\\b" + java.util.regex.Pattern.quote(original) + "\\b", "Test");

        return normalizeWhitespacePreserveNewlines(renamed);
    }

    private static String normalizeWhitespacePreserveNewlines(String code) {
        // Keep line structure for readability in JSON, but normalize within lines.
        // - Normalize CRLF -> LF
        // - Strip trailing spaces
        // - Collapse runs of spaces/tabs inside each line
        if (code == null || code.isEmpty()) return "";
        String lf = code.replace("\r\n", "\n").replace("\r", "\n");
        return lf.lines()
                .map(line -> line
                        .replaceAll("[\\t ]+", " ")
                        .replaceAll("\\s+$", "")
                )
                .collect(java.util.stream.Collectors.joining("\n"))
                .trim();
    }

    private static String normalizeWhitespace(String code) {
        // Collapse multiple spaces, tabs, newlines, etc. into a single space.
        return code.replaceAll("[\\s]+", " ").trim();
    }

    /** Represents either field 'value' or zero-arg method 'value()'. Only one may exist. */
    private record ValueAccessor(Field field, Method method) {
        static ValueAccessor forClass(Class<?> clazz) {
            Field f = null;
            Method m = null;

            try {
                f = clazz.getDeclaredField("value");
                if (Modifier.isStatic(f.getModifiers())) f = null;
            } catch (NoSuchFieldException ignored) {
            }

            for (Method cand : clazz.getDeclaredMethods()) {
                if (cand.getName().equals("value") && cand.getParameterCount() == 0 && !Modifier.isStatic(cand.getModifiers())) {
                    m = cand;
                    break;
                }
            }

            if (f != null && m != null) {
                throw new IllegalArgumentException(clazz.getName() + " defines both field 'value' and method 'value()'; only one is supported");
            }
            if (f == null && m == null) {
                throw new IllegalArgumentException(clazz.getName() + " must define either field 'value' or method 'value()'");
            }
            if (f != null) f.setAccessible(true);
            if (m != null) m.setAccessible(true);
            return new ValueAccessor(f, m);
        }

        Object getValue(Object instance) throws Exception {
            if (field != null) return field.get(instance);
            return method.invoke(instance);
        }
    }

    private static String filterJolNoise(String s) {
        if (s == null) return null;

        // Drop leading blank lines first.
        List<String> lines = s.lines().toList();
        int start = 0;
        while (start < lines.size() && lines.get(start).isBlank()) start++;

        // Skip the first non-blank line (often a noisy header like "... object externals:" / "... footprint:").
        if (start < lines.size()) start++;

        return lines.subList(Math.min(start, lines.size()), lines.size()).stream()
                .filter(l -> !l.contains(" object externals:"))
                .filter(l -> !l.contains(" footprint:"))
                .collect(Collectors.joining("\n"))
                .trim();
    }
}