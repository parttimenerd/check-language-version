package me.bechberger.sizes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON model + parsers for the program results that we emit.
 */
public final class ProgramResults {

    private ProgramResults() {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ProgramResult(
            @JsonProperty("class") String className,
            @JsonProperty("code") String code,
            @JsonProperty("sanitizedCode") String sanitizedCode,
            @JsonProperty("rating") Integer rating,
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("layout") List<LayoutResult> layout
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record LayoutResult(
            @JsonProperty("totalSize") Long totalSize,

            @JsonProperty("layout") List<GraphRow> layout,
            @JsonProperty("footprint") List<FootprintRow> footprint,

            // Parsed output of ClassLayout.parseInstance(x).toPrintable() for the root value
            // and for all reachable, non-primitive field values (cycle-safe).
            @JsonProperty("classLayout") List<ParsedClassLayout> classLayout,

            @JsonProperty("UseCompactObjectHeaders") Boolean useCompactObjectHeaders
    ) {
    }

    /** Parsed form of ClassLayout.parseInstance(x).toPrintable() ("object internals"). */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ParsedClassLayout(
            @JsonProperty("type") String type,
            @JsonProperty("rows") List<ClassLayoutRow> rows,
            @JsonProperty("instanceSize") Long instanceSize,
            @JsonProperty("spaceLosses") SpaceLosses spaceLosses
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SpaceLosses(
            @JsonProperty("internal") Long internal,
            @JsonProperty("external") Long external,
            @JsonProperty("total") Long total
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ClassLayoutRow(
            @JsonProperty("offset") long offset,
            @JsonProperty("size") long size,
            @JsonProperty("type") String type,
            @JsonProperty("description") String description,
            @JsonProperty("value") String value
    ) {
    }

    public record GraphRow(
            @JsonProperty("size") long size,
            @JsonProperty("type") String type,
            @JsonProperty("path") String path,
            @JsonProperty("value") String value
    ) {
    }

    public record FootprintRow(
            @JsonProperty("count") long count,
            @JsonProperty("avg") Long avg,
            @JsonProperty("sum") Long sum,
            @JsonProperty("description") String description
    ) {
    }

    public static List<GraphRow> parseGraphLayoutPrintable(String printable) {
        // Best-effort parser for GraphLayout#toPrintable().
        // Expected columns: ADDRESS SIZE TYPE PATH VALUE
        var rows = new ArrayList<GraphRow>();
        if (printable == null) return rows;

        boolean headerSeen = false;
        for (String line : printable.lines().toList()) {
            String t = line.trim();
            if (t.isEmpty()) continue;
            if (t.startsWith("ADDRESS") && t.contains("SIZE") && t.contains("TYPE")) {
                headerSeen = true;
                continue;
            }
            if (!headerSeen) continue;

            // Parse: {address} {size} {type...} {path...} {value...}
            // Note: TYPE can contain spaces when it is wrapped in parentheses, e.g. "(something else)".
            String[] first = t.split("\\s+", 3);
            if (first.length < 2) continue;

            String sizeStr = first[1];
            long size;
            try {
                size = Long.parseLong(sizeStr);
            } catch (Exception e) {
                continue;
            }

            String afterSize = first.length == 3 ? first[2] : "";
            if (afterSize.isBlank()) continue;

            String type;
            String rest;
            String as = afterSize.trim();
            if (as.startsWith("(")) {
                // Consume until the first ')' and treat that as the full TYPE token.
                int end = as.indexOf(')');
                if (end >= 0) {
                    type = as.substring(0, end + 1);
                    rest = as.substring(end + 1).trim();
                } else {
                    // Unbalanced, fall back to whitespace tokenization.
                    String[] typeAndRest = as.split("\\s+", 2);
                    type = typeAndRest[0];
                    rest = typeAndRest.length == 2 ? typeAndRest[1] : "";
                }
            } else {
                String[] typeAndRest = as.split("\\s+", 2);
                type = typeAndRest[0];
                rest = typeAndRest.length == 2 ? typeAndRest[1] : "";
            }

            // rest is typically: PATH(whitespace...)VALUE. If no path, value starts immediately.
            String path;
            String value;
            int twoSpaces = indexOfMinTwoSpaces(rest);
            if (twoSpaces >= 0) {
                path = rest.substring(0, twoSpaces).trim();
                value = rest.substring(twoSpaces).trim();
            } else {
                path = "";
                value = rest.trim();
            }

            rows.add(new GraphRow(size, normalizeGraphType(type), path, value));
        }
        return rows;
    }

    private static String normalizeGraphType(String rawType) {
        if (rawType == null) return "";
        String t = rawType.trim();
        if (t.isEmpty()) return "";

        // JOL prints inner classes as Outer$Inner, often with an FQCN prefix.
        // For our JSON output we want to omit the outer test/program class name.
        int lastDollar = t.lastIndexOf('$');
        if (lastDollar >= 0 && lastDollar + 1 < t.length()) {
            return t.substring(lastDollar + 1);
        }
        return t;
    }

    private static int indexOfMinTwoSpaces(String s) {
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == ' ' && s.charAt(i + 1) == ' ') return i;
        }
        return -1;
    }

    public static List<FootprintRow> parseFootprint(String footprint) {
        // Best-effort parser for GraphLayout#toFootprint():
        // COUNT AVG SUM DESCRIPTION
        var rows = new ArrayList<FootprintRow>();
        if (footprint == null) return rows;

        boolean headerSeen = false;
        for (String line : footprint.lines().toList()) {
            String t = line.trim();
            if (t.isEmpty()) continue;
            if (t.startsWith("COUNT") && t.contains("AVG") && t.contains("SUM")) {
                headerSeen = true;
                continue;
            }
            if (!headerSeen) continue;
            if (t.startsWith("(") || t.contains("(total")) {
                // ignore total line
                continue;
            }

            String[] parts = t.split("\\s+", 4);
            if (parts.length < 4) continue;

            long count;
            Long avg;
            Long sum;
            try {
                count = Long.parseLong(parts[0]);
            } catch (Exception e) {
                continue;
            }
            avg = parseLongOrNull(parts[1]);
            sum = parseLongOrNull(parts[2]);
            String desc = normalizeGraphType(parts[3]);

            rows.add(new FootprintRow(count, avg, sum, desc));
        }
        return rows;
    }

    public static Long extractTotalSizeFromFootprint(String footprint) {
        // Typical total line format:
        // "         3                 56   (total)"
        // In that example, SUM=56 and AVG is blank.
        if (footprint == null) return null;

        boolean headerSeen = false;
        for (String line : footprint.lines().toList()) {
            String t = line.trim();
            if (t.isEmpty()) continue;
            if (t.startsWith("COUNT") && t.contains("AVG") && t.contains("SUM")) {
                headerSeen = true;
                continue;
            }
            if (!headerSeen) continue;
            if (!t.contains("(total")) continue;

            String[] parts = t.split("\\s+", 4);
            // parts could be [COUNT, SUM, (total)] if AVG is missing and split collapses whitespace.
            if (parts.length >= 3) {
                // find last numeric token before description
                for (int i = parts.length - 2; i >= 0; i--) {
                    Long v = parseLongOrNull(parts[i]);
                    if (v != null) return v;
                }
            }
        }
        return null;
    }

    public static Long extractOwnSizeFromPrintableGraph(String printable) {
        // Attempts to parse the first row after the header where PATH is "(object)".
        // That row describes the root instance itself.
        if (printable == null) return null;

        boolean headerSeen = false;
        for (String line : printable.lines().toList()) {
            String t = line.trim();
            if (t.isEmpty()) continue;
            if (t.startsWith("ADDRESS") && t.contains("SIZE") && t.contains("TYPE")) {
                headerSeen = true;
                continue;
            }
            if (!headerSeen) continue;

            String[] parts = t.split("\\s+", 4);
            if (parts.length < 3) continue;
            Long size = parseLongOrNull(parts[1]);
            if (size == null) continue;
            String rest = parts.length == 4 ? parts[3] : "";
            if (rest.contains("(object)")) {
                return size;
            }
        }
        return null;
    }

    private static Long parseLongOrNull(String v) {
        try {
            return Long.parseLong(v);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Best-effort parser for {@code ClassLayout#toPrintable()}.
     *
     * <p>Alignment rules in the table:
     * <ul>
     *   <li>OFF, SZ, TYPE are right-aligned within their columns</li>
     *   <li>DESCRIPTION and VALUE are left-aligned</li>
     * </ul>
     */
    public static ParsedClassLayout parseClassLayoutPrintable(String printable) {
        if (printable == null || printable.isBlank()) {
            return new ParsedClassLayout(null, List.of(), null, null);
        }

        String typeLine = null;
        var rows = new ArrayList<ClassLayoutRow>();
        Long instanceSize = null;
        SpaceLosses spaceLosses = null;

        ClassLayoutColumns cols = null;
        boolean tableSeen = false;

        for (String raw : printable.lines().toList()) {
            String t = raw.trim();
            if (t.isEmpty()) continue;

            if (typeLine == null && t.endsWith(" object internals:")) {
                typeLine = t;
                continue;
            }

            if (!tableSeen && isClassLayoutHeaderLine(t)) {
                cols = ClassLayoutColumns.fromHeaderLine(raw);
                tableSeen = cols != null;
                continue;
            }

            if (t.startsWith("Instance size:")) {
                var m = java.util.regex.Pattern.compile("Instance size:\\s*(\\d+)\\s*bytes").matcher(t);
                if (m.find()) instanceSize = Long.parseLong(m.group(1));
                tableSeen = false;
                continue;
            }
            if (t.startsWith("Space losses:")) {
                // Example:
                // Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
                var m = java.util.regex.Pattern.compile(
                        "Space losses:\\s*(\\d+)\\s*bytes internal\\s*\\+\\s*(\\d+)\\s*bytes external\\s*=\\s*(\\d+)\\s*bytes total"
                ).matcher(t);
                if (m.find()) {
                    spaceLosses = new SpaceLosses(
                            Long.parseLong(m.group(1)),
                            Long.parseLong(m.group(2)),
                            Long.parseLong(m.group(3))
                    );
                }
                continue;
            }

            if (!tableSeen || cols == null) continue;

            String offStr = cols.get("OFF", raw);
            String szStr = cols.get("SZ", raw);
            String type = cols.get("TYPE", raw);
            String desc = cols.get("DESCRIPTION", raw);
            String val = cols.get("VALUE", raw);

            long off;
            long sz;
            try {
                assert offStr != null;
                off = Long.parseLong(offStr);
                assert szStr != null;
                sz = Long.parseLong(szStr);
            } catch (Exception e) {
                continue;
            }

            rows.add(new ClassLayoutRow(off, sz, type, desc, val));
        }

        String typeName = null;
        if (typeLine != null) {
            typeName = typeLine.substring(0, typeLine.length() - " object internals:".length()).trim();
        }
        return new ParsedClassLayout(typeName, rows, instanceSize, spaceLosses);
    }

    private static boolean isClassLayoutHeaderLine(String trimmedLine) {
        return trimmedLine.startsWith("OFF")
                && trimmedLine.contains("SZ")
                && trimmedLine.contains("TYPE")
                && trimmedLine.contains("DESCRIPTION");
    }

    private record Column(String name, int from, int to) {
        String slice(String row) {
            if (from > row.length()) {
                return "";
            }
            if (to == -1) {
                return row.substring(from).trim();
            }
            return row.substring(from, Math.min(to, row.length())).trim();
        }
    }

    private static final class ClassLayoutColumns {
        private final List<Column> cols;

        private ClassLayoutColumns(List<Column> cols) {
            this.cols = cols;
        }

        static ClassLayoutColumns fromHeaderLine(String rawHeaderLine) {
            if (rawHeaderLine == null) return null;

            List<Column> cols = new ArrayList<>();
            int startOfOFF = rawHeaderLine.indexOf("OFF");
            if (startOfOFF < 0) return null;
            int startOfSZ = rawHeaderLine.indexOf("SZ", startOfOFF);
            int startOfTYPE = rawHeaderLine.indexOf("TYPE", startOfSZ);
            int startOfDESC = rawHeaderLine.indexOf("DESCRIPTION", startOfTYPE);
            int startOfVALUE = rawHeaderLine.indexOf("VALUE", startOfDESC);

            cols.add(new Column("OFF", 0, startOfOFF + 3));
            cols.add(new Column("SZ", startOfOFF + 3, startOfSZ + 2));
            cols.add(new Column("TYPE", startOfSZ + 2, startOfTYPE + 4));
            cols.add(new Column("DESCRIPTION", startOfDESC, startOfVALUE));
            cols.add(new Column("VALUE", startOfVALUE, -1));
            return new ClassLayoutColumns(cols);
        }

        public String get(String name, String row) {
            for (Column c : cols) {
                if (c.name.equals(name)) {
                    return c.slice(row);
                }
            }
            return null;
        }
    }
}