package me.bechberger.check;

import me.bechberger.check.FeatureChecker.JavaFeature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Loads Markdown descriptions for {@link JavaFeature} from individual resource files.
 *
 * <p>Resource format (per-feature markdown file):
 * <pre>
 *   # This is a comment and will be ignored
 *
 *   Actual markdown content for the feature goes here.
 *   It can span multiple lines.
 * </pre>
 *
 * <p>Fail-fast behavior:
 * <ul>
 *   <li>Throws at class-load time if any {@link JavaFeature} has no corresponding file.</li>
 *   <li>Throws at class-load time if any file is empty or only contains comments.</li>
 * </ul>
 */
public final class FeatureMarkdownDescriptions {

    private static final String FEATURES_DIR = "/me/bechberger/check/features";

    /**
     * Full markdown by feature. Guaranteed to contain an entry for every {@link JavaFeature}.
     */
    public static final Map<JavaFeature, String> MARKDOWN_BY_FEATURE = loadStrict();

    private FeatureMarkdownDescriptions() {
    }

    public static String markdown(JavaFeature feature) {
        return Objects.requireNonNull(MARKDOWN_BY_FEATURE.get(feature), "Missing markdown for " + feature.name());
    }

    private static Map<JavaFeature, String> loadStrict() {
        Map<JavaFeature, String> map = new EnumMap<>(JavaFeature.class);

        for (JavaFeature feature : JavaFeature.values()) {
            String resourcePath = FEATURES_DIR + "/" + feature.name() + ".md";
            String text = readResourceStrict(resourcePath, feature);
            map.put(feature, text);
        }

        return Map.copyOf(map);
    }

    private static String readResourceStrict(String resourcePath, JavaFeature feature) {
        try (InputStream in = FeatureMarkdownDescriptions.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException("Missing resource: " + resourcePath + " (for " + feature.name() + ")");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder buf = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    // allow comments in these markdown snippets too
                    if (line.startsWith("#")) {
                        continue;
                    }
                    buf.append(line).append('\n');
                }
                String text = buf.toString();
                if (text.endsWith("\n")) {
                    text = text.substring(0, text.length() - 1);
                }
                if (text.isBlank()) {
                    throw new IllegalStateException("Empty markdown body for '" + feature.name() + "' in " + resourcePath);
                }
                return text;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + resourcePath + ": " + e.getMessage(), e);
        }
    }
}