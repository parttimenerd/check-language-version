package me.bechberger.sizes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.bechberger.minicli.MiniCli;
import me.bechberger.minicli.Spec;
import me.bechberger.minicli.annotations.Command;
import me.bechberger.minicli.annotations.Option;
import org.openjdk.jol.info.ClassLayout;
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
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
        var classLayouts = collectClassLayouts(value);
        var layoutResult = new ProgramResults.LayoutResult(
                totalSize,
                parsedLayout,
                parsedFootprint,
                classLayouts,
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

    /**
     * Collect ClassLayout#printable for {@code root} and for all reachable non-primitive field values.
     *
     * <p>Traversal is best-effort:
     * <ul>
     *   <li>Cycle-safe via identity tracking</li>
     *   <li>Skips primitives and primitive arrays</li>
     *   <li>Traverses arrays of references</li>
     *   <li>Traverses instance fields (including private), stopping at Object</li>
     * </ul>
     */
    private static List<ProgramResults.ParsedClassLayout> collectClassLayouts(Object root) {
        if (root == null) return List.of();

        // Keep a stable insertion order for nicer diffs.
        Map<String, ProgramResults.ParsedClassLayout> byType = new LinkedHashMap<>();
        IdentityHashMap<Object, Boolean> seen = new IdentityHashMap<>();

        ArrayList<Object> work = new ArrayList<>();
        work.add(root);

        // Use the example program base class name as the prefix to strip.
        // JOL can emit types using either '$' or '.' to separate nested classes.
        // Examples:
        // - me...MultiRecord$Person -> Person
        // - me...SelfCycle$Node or me...SelfCycle.Node -> Node
        // - me...ThreeNodeCycle.Node -> Node
        String outerToStrip = normalizeProgramBaseClassName(root.getClass().getName());

        while (!work.isEmpty()) {
            Object obj = work.remove(work.size() - 1);
            if (obj == null) continue;
            if (seen.put(obj, Boolean.TRUE) != null) continue;

            Class<?> c = obj.getClass();

            String fullType = c.getName();
            String normalizedType = normalizeCollectedLayoutType(fullType, outerToStrip);

            if (!byType.containsKey(normalizedType)) {
                try {
                    String printable = ClassLayout.parseInstance(obj).toPrintable();
                    var parsed = ProgramResults.parseClassLayoutPrintable(printable);

                    // Ensure the type is always present and normalized.
                    parsed = new ProgramResults.ParsedClassLayout(
                            normalizedType,
                            parsed.rows(),
                            parsed.instanceSize(),
                            parsed.spaceLosses()
                    );

                    byType.put(normalizedType, parsed);
                } catch (Throwable t) {
                    // Fallback: still record that we saw this type.
                    byType.put(normalizedType, new ProgramResults.ParsedClassLayout(normalizedType, List.of(), null, null));
                }
            }

            // Traverse references.
            if (c.isArray()) {
                Class<?> comp = c.getComponentType();
                if (!comp.isPrimitive() && obj instanceof Object[] arr) {
                    for (Object el : arr) {
                        if (el != null) work.add(el);
                    }
                }
                continue;
            }

            // Skip Class objects (huge graph) and enums (often singletons shared widely).
            if (obj instanceof Class<?> || c.isEnum()) {
                continue;
            }

            // Records: also traverse via record component accessors.
            // This is important because reflective field access can fail under stronger encapsulation.
            if (c.isRecord()) {
                try {
                    for (var rc : c.getRecordComponents()) {
                        Class<?> t = rc.getType();
                        if (t.isPrimitive()) continue;
                        var accessor = rc.getAccessor();
                        if (accessor == null) continue;
                        accessor.setAccessible(true);
                        Object v = accessor.invoke(obj);
                        if (v != null) work.add(v);
                    }
                } catch (Throwable ignored) {
                    // best-effort
                }
            }

            // Walk class hierarchy and enqueue non-primitive field values.
            for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass()) {
                Field[] fields;
                try {
                    fields = k.getDeclaredFields();
                } catch (Throwable t) {
                    continue;
                }
                for (Field f : fields) {
                    int mod = f.getModifiers();
                    if (Modifier.isStatic(mod)) continue;
                    if (f.getType().isPrimitive()) continue;
                    try {
                        f.setAccessible(true);
                        Object v = f.get(obj);
                        if (v != null) work.add(v);
                    } catch (Throwable t) {
                        // ignore inaccessible/broken fields
                    }
                }
            }
        }

        return byType.values().stream().filter(v -> v != null).toList();
    }

    /**
     * Determine the base class name of the example program from a runtime class name.
     *
     * <p>We want to strip the outer program name from nested types in class layout output.
     * For instance:
     * <ul>
     *   <li>me...MultiRecord$Person -> me...MultiRecord</li>
     *   <li>me...ThreeNodeCycle.Node -> me...ThreeNodeCycle</li>
     * </ul>
     */
    private static String normalizeProgramBaseClassName(String className) {
        if (className == null) return "";
        String base = className;
        int dollar = base.indexOf('$');
        if (dollar >= 0) base = base.substring(0, dollar);

        // If the root class is itself a nested class printed with '.', strip trailing segments
        // to the first segment after the package.
        // Heuristic: for "pkg.Outer.Inner" we turn it into "pkg.Outer".
        int lastDot = base.lastIndexOf('.');
        if (lastDot >= 0) {
            // keep package + first simple name
            String pkg = base.substring(0, lastDot);
            String simple = base.substring(lastDot + 1);
            int innerDot = simple.indexOf('.');
            if (innerDot >= 0) {
                base = pkg + "." + simple.substring(0, innerDot);
            }
        }
        return base;
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
        // Keep original whitespace as much as possible.
        // - Normalize CRLF -> LF
        // - Trim leading/trailing blank lines
        // IMPORTANT: Do NOT collapse multiple spaces/tabs inside lines.
        if (code == null || code.isEmpty()) return "";
        String lf = code.replace("\r\n", "\n").replace("\r", "\n");
        return lf.lines()
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

    private static String normalizeCollectedLayoutType(String fullType, String outerToStrip) {
        if (fullType == null) return "";

        String t = fullType;

        // Strip the example program base class prefix, if present.
        if (outerToStrip != null && !outerToStrip.isBlank()) {
            String dollarPrefix = outerToStrip + "$";
            String dotPrefix = outerToStrip + ".";
            if (t.startsWith(dollarPrefix)) {
                t = t.substring(dollarPrefix.length());
            } else if (t.startsWith(dotPrefix)) {
                t = t.substring(dotPrefix.length());
            }
        }

        // Always reduce to the innermost name if nested separators remain.
        int lastDollar = t.lastIndexOf('$');
        if (lastDollar >= 0 && lastDollar + 1 < t.length()) {
            t = t.substring(lastDollar + 1);
        }
        int lastDot = t.lastIndexOf('.');
        if (lastDot >= 0 && lastDot + 1 < t.length()) {
            t = t.substring(lastDot + 1);
        }
        return t;
    }
}