package me.bechberger.check;

import me.bechberger.check.FeatureChecker.JavaFeature;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validates that all HTTP(S) links embedded in feature markdown are reachable.
 *
 * <p>This test is network-dependent. Disable it with:
 * <ul>
 *   <li>-DskipLinkChecks=true</li>
 * </ul>
 *
 * <p>To avoid repeatedly hitting third-party websites, this test uses a tiny on-disk cache.
 * Configure via system properties:
 * <ul>
 *   <li>-DlinkCheckCacheFile=... (default: target/link-check-cache.tsv)</li>
 *   <li>-DlinkCheckCacheTtlHours=... (default: 168 = 7 days)</li>
 * </ul>
 */
class FeatureMarkdownLinksTest {

    /**
     * Matches Markdown links only: [label](https://example.com)
     * Spec requirement: no bare URLs are allowed in the markdown.
     */
    private static final Pattern MARKDOWN_LINK_PATTERN =
            Pattern.compile("\\[([^]]{3,})]\\((?<url>https?://[^\\s)]+)\\)");

    private static final Path CACHE_FILE = Path.of(
            System.getProperty("linkCheckCacheFile", "link-check-cache.tsv"));

    /** Default: 7 days */
    private static final long CACHE_TTL_MILLIS = Duration.ofHours(
            Long.getLong("linkCheckCacheTtlHours", 24L * 7L)
    ).toMillis();

    /**
     * If true, don't perform any network calls; only use cached results.
     * This is useful in CI when the cache is restored and outbound network is restricted.
     */
    private static final boolean OFFLINE = Boolean.getBoolean("linkCheckOffline");

    /**
     * Committed whitelist: URLs in this file are treated as valid and won't be checked.
     * This is useful for known-good but flaky URLs (e.g., GitHub HTML pages that sometimes time out).
     */
    private static final Path WHITELIST_FILE = Path.of(
            System.getProperty("linkCheckWhitelistFile", "link-check-whitelist.txt"));

    private static Set<String> loadWhitelist() {
        if (!Files.exists(WHITELIST_FILE)) {
            return Set.of();
        }
        try {
            Set<String> urls = new LinkedHashSet<>();
            for (String line : Files.readAllLines(WHITELIST_FILE, StandardCharsets.UTF_8)) {
                String s = line.trim();
                if (s.isEmpty() || s.startsWith("#")) {
                    continue;
                }
                urls.add(s);
            }
            return Collections.unmodifiableSet(urls);
        } catch (IOException ignored) {
            return Set.of();
        }
    }

    @Test
    void allLinksInFeatureMarkdownAreReachable() throws IOException {
        Assumptions.assumeFalse(Boolean.getBoolean("skipLinkChecks"), "skipLinkChecks=true");

        Set<String> whitelist = loadWhitelist();

        // Load cache once
        LinkCheckCache cache = LinkCheckCache.load(CACHE_FILE);

        Map<JavaFeature, String> mdByFeature = FeatureMarkdownDescriptions.MARKDOWN_BY_FEATURE;
        List<String> failures = new ArrayList<>();

        // De-duplicate links to speed things up (many features will share Oracle base URLs)
        Map<String, List<JavaFeature>> urlToFeatures = new LinkedHashMap<>();
        for (Map.Entry<JavaFeature, String> entry : mdByFeature.entrySet()) {
            JavaFeature feature = entry.getKey();
            for (String url : extractUrls(entry.getValue())) {
                urlToFeatures.computeIfAbsent(url, __ -> new ArrayList<>()).add(feature);
            }
        }

        boolean cacheDirty = false;
        long now = System.currentTimeMillis();

        for (String url : urlToFeatures.keySet()) {
            try {
                if (whitelist.contains(url)) {
                    // Explicitly allowed; skip checks.
                    continue;
                }

                LinkCheckResult result = cache.getIfFresh(url, now, CACHE_TTL_MILLIS);
                if (result == null) {
                    if (OFFLINE) {
                        failures.add(url + " -> not in cache (offline mode)" + " (features: " + urlToFeatures.get(url) + ")");
                        continue;
                    }
                    result = checkUrlReachable(url);
                    cache.put(url, now, result);
                    cacheDirty = true;
                }

                if (!result.ok()) {
                    failures.add(url + " -> " + result.message() + " (features: " + urlToFeatures.get(url) + ")");
                }
            } catch (Exception e) {
                LinkCheckResult result = new LinkCheckResult(false, "exception: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                cache.put(url, now, result);
                cacheDirty = true;
                failures.add(url + " -> " + result.message() + " (features: " + urlToFeatures.get(url) + ")");
            }
        }

        if (cacheDirty) {
            cache.save(CACHE_FILE);
        }

        if (!failures.isEmpty()) {
            fail("Unreachable/bad links (set -DskipLinkChecks=true to skip):\n" + String.join("\n", failures));
        }
    }

    private static Set<String> extractUrls(String markdown) {
        Set<String> urls = new LinkedHashSet<>();
        Matcher m = MARKDOWN_LINK_PATTERN.matcher(markdown);
        while (m.find()) {
            String url = m.group("url");
            if (url == null) {
                continue;
            }
            // Strip simple trailing punctuation (common in prose)
            url = stripTrailingPunctuation(url);
            urls.add(url);
        }
        return urls;
    }

    private static String stripTrailingPunctuation(String url) {
        while (!url.isEmpty()) {
            char c = url.charAt(url.length() - 1);
            if (c == '.' || c == ',' || c == ';' || c == ':') {
                url = url.substring(0, url.length() - 1);
                continue;
            }
            return url;
        }
        return url;
    }

    private record LinkCheckResult(boolean ok, String message) {
    }

    private static final class LinkCheckCache {
        // url -> entry
        private final Map<String, CacheEntry> entries = new LinkedHashMap<>();

        static LinkCheckCache load(Path path) {
            LinkCheckCache cache = new LinkCheckCache();
            if (!Files.exists(path)) {
                return cache;
            }
            try {
                for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                    if (line.isBlank() || line.startsWith("#")) {
                        continue;
                    }
                    // tsMillis \t ok \t message \t url
                    String[] parts = line.split("\t", 4);
                    if (parts.length != 4) {
                        continue;
                    }
                    long ts;
                    try {
                        ts = Long.parseLong(parts[0]);
                    } catch (NumberFormatException ignored) {
                        continue;
                    }
                    boolean ok = "1".equals(parts[1]);
                    String message = parts[2];
                    String url = parts[3];
                    cache.entries.put(url, new CacheEntry(ts, new LinkCheckResult(ok, message)));
                }
            } catch (IOException ignored) {
                // Treat cache as best-effort.
            }
            return cache;
        }

        LinkCheckResult getIfFresh(String url, long nowMillis, long ttlMillis) {
            CacheEntry e = entries.get(url);
            if (e == null) return null;
            if (OFFLINE) {
                // In offline mode, the cache is treated as a committed allowlist.
                // Don't apply TTL, otherwise CI/offline runs can become flaky.
                return e.result;
            }
            if (ttlMillis <= 0) return null;
            if (nowMillis - e.timestampMillis > ttlMillis) return null;
            return e.result;
        }

        void put(String url, long nowMillis, LinkCheckResult result) {
            // Prefer successful results if we already have a failure for this URL.
            // This supports checked-in "whitelist" entries (ok=true) that should override flaky timeouts.
            CacheEntry existing = entries.get(url);
            if (existing != null && !existing.result.ok() && result.ok()) {
                entries.put(url, new CacheEntry(nowMillis, result));
                return;
            }
            entries.put(url, new CacheEntry(nowMillis, result));
        }

        void save(Path path) throws IOException {
            Files.createDirectories(path.getParent() != null ? path.getParent() : Path.of("."));
            List<String> out = new ArrayList<>();
            out.add("# tsMillis\tok\tmessage\turl");
            for (Map.Entry<String, CacheEntry> e : entries.entrySet()) {
                CacheEntry ce = e.getValue();
                // Keep messages single-line to stay TSV.
                String msg = ce.result.message().replace("\n", " ").replace("\r", " ").replace("\t", " ");
                out.add(ce.timestampMillis + "\t" + (ce.result.ok() ? "1" : "0") + "\t" + msg + "\t" + e.getKey());
            }
            Files.write(path, out, StandardCharsets.UTF_8);
        }

        private record CacheEntry(long timestampMillis, LinkCheckResult result) {
        }
    }

    private static LinkCheckResult checkUrlReachable(String url) throws URISyntaxException, IOException {
        URI uri = new URI(url);
        if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
            return new LinkCheckResult(false, "unsupported scheme");
        }

        // Prefer HEAD (cheap). Some hosts disallow HEAD; fall back to GET.
        LinkCheckResult head = tryRequest(uri.toURL(), "HEAD");
        if (head.ok) {
            return head;
        }
        // fallback
        return tryRequest(uri.toURL(), "GET");
    }

    private static LinkCheckResult tryRequest(URL url, String method) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setRequestMethod(method);
        conn.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        conn.setReadTimeout((int) Duration.ofSeconds(10).toMillis());
        conn.setRequestProperty("User-Agent", "check-language-version-link-check/1.0");
        conn.connect();

        int code = conn.getResponseCode();
        // Treat 2xx and 3xx as ok.
        if (code >= 200 && code < 400) {
            return new LinkCheckResult(true, method + " " + code);
        }
        return new LinkCheckResult(false, method + " " + code);
    }
}