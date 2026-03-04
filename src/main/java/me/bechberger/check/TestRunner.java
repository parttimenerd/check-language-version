package me.bechberger.check;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Formalized test runner that reads test specifications from comments in test files.
 *
 * Test files should include a comment block at the top with the following format:
 * // Test: <description>
 * // Expected Version: <number>
 * // Required Features: <feature1>, <feature2>, ...
 * // Optional Features: <feature1>, <feature2>, ...
 * // Forbidden Features: <feature1>, <feature2>, ...
 */
public class TestRunner {

    static class TestSpec {
        String description;
        Integer expectedVersion;
        Set<String> requiredFeatures = new HashSet<>();
        Set<String> optionalFeatures = new HashSet<>();
        Set<String> forbiddenFeatures = new HashSet<>();
        File file;

        @Override
        public String toString() {
            return "TestSpec{" +
                "file=" + file.getName() +
                ", expectedVersion=" + expectedVersion +
                ", required=" + requiredFeatures.size() +
                ", optional=" + optionalFeatures.size() +
                ", forbidden=" + forbiddenFeatures.size() +
                '}';
        }
    }

    static class TestResult {
        TestSpec spec;
        FeatureChecker.FeatureCheckResult checkResult;
        boolean parseFailed;
        boolean versionMatches;
        Set<String> missingRequired = new HashSet<>();
        Set<String> presentForbidden = new HashSet<>();

        boolean passed() {
            return !parseFailed &&
                   versionMatches &&
                   missingRequired.isEmpty() &&
                   presentForbidden.isEmpty();
        }
    }

    /**
     * Parse test specification from file comments
     */
    public static TestSpec parseTestSpec(File file) throws IOException {
        TestSpec spec = new TestSpec();
        spec.file = file;

        List<String> lines = Files.readAllLines(file.toPath());

        for (String line : lines) {
            line = line.trim();

            // Stop at first non-comment line
            if (!line.startsWith("//")) {
                break;
            }

            // Remove comment prefix
            String content = line.substring(2).trim();

            if (content.startsWith("Test:")) {
                spec.description = content.substring(5).trim();
            } else if (content.startsWith("Expected Version:") || content.startsWith("Version:")) {
                String versionStr = content.split(":")[1].trim();
                try {
                    spec.expectedVersion = Integer.parseInt(versionStr);
                } catch (NumberFormatException e) {
                    // ignore
                }
            } else if (content.startsWith("Required Features:") || content.startsWith("Required:")) {
                String features = content.substring(content.indexOf(':') + 1).trim();
                spec.requiredFeatures.addAll(parseFeatureList(features));
            } else if (content.startsWith("Optional Features:") || content.startsWith("Optional:")) {
                String features = content.substring(content.indexOf(':') + 1).trim();
                spec.optionalFeatures.addAll(parseFeatureList(features));
            } else if (content.startsWith("Forbidden Features:") || content.startsWith("Forbidden:")) {
                String features = content.substring(content.indexOf(':') + 1).trim();
                spec.forbiddenFeatures.addAll(parseFeatureList(features));
            }
        }

        // Fallback to filename-based version detection if not specified
        if (spec.expectedVersion == null) {
            spec.expectedVersion = extractVersionFromFilename(file.getName());
        }

        return spec;
    }

    private static Set<String> parseFeatureList(String features) {
        if (features.isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(features.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(toSet());
    }

    private static Integer extractVersionFromFilename(String filename) {
        // Try "JavaX_..." pattern
        if (filename.startsWith("Java")) {
            int underscoreIdx = filename.indexOf('_');
            if (underscoreIdx > 4) {
                try {
                    return Integer.parseInt(filename.substring(4, underscoreIdx));
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }

        // Try "..._JavaX.java" pattern
        int javaIdx = filename.lastIndexOf("_Java");
        if (javaIdx > 0) {
            int dotIdx = filename.lastIndexOf('.');
            if (dotIdx > javaIdx + 5) {
                try {
                    return Integer.parseInt(filename.substring(javaIdx + 5, dotIdx));
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }

        return null;
    }

    /**
     * Run a single test
     */
    public static TestResult runTest(TestSpec spec) {
        TestResult result = new TestResult();
        result.spec = spec;

        try {
            result.checkResult = FeatureChecker.check(spec.file);

            if (result.checkResult == null) {
                result.parseFailed = true;
                return result;
            }

            // Check version
            if (spec.expectedVersion != null) {
                result.versionMatches =
                    result.checkResult.requiredJavaVersion() == spec.expectedVersion ||
                    result.checkResult.features().stream()
                        .anyMatch(f -> f.getJavaVersion() == spec.expectedVersion);
            } else {
                result.versionMatches = true;
            }

            // Get detected feature names
            Set<String> detectedFeatures = result.checkResult.features().stream()
                .map(FeatureChecker.JavaFeature::name)
                .collect(toSet());

            // Check required features
            for (String required : spec.requiredFeatures) {
                if (!detectedFeatures.contains(required)) {
                    result.missingRequired.add(required);
                }
            }

            // Check forbidden features
            for (String forbidden : spec.forbiddenFeatures) {
                if (detectedFeatures.contains(forbidden)) {
                    result.presentForbidden.add(forbidden);
                }
            }

        } catch (FileNotFoundException e) {
            result.parseFailed = true;
        } catch (FeatureChecker.ParseFailureException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * Run tests in a directory
     */
    public static Map<String, TestResult> runTestsInDirectory(File dir) throws IOException {
        File[] files = dir.listFiles((d, name) -> name.endsWith(".java"));
        if (files == null) {
            return emptyMap();
        }

        return Arrays.stream(files).sorted()
                .collect(toMap(
                        File::getName,
                        file -> {
                            try {
                                TestSpec spec = parseTestSpec(file);
                                return runTest(spec);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    /**
     * Print test results
     */
    public static void printResults(Map<String, TestResult> results) {
        int passed = 0;
        int failed = 0;
        int parseFailed = 0;

        for (Map.Entry<String, TestResult> entry : results.entrySet()) {
            TestResult result = entry.getValue();

            if (result.parseFailed) {
                System.out.printf("%-50s PARSE FAILED\n", entry.getKey());
                parseFailed++;
            } else if (result.passed()) {
                System.out.printf("%-50s ✓ PASS\n", entry.getKey());
                passed++;
            } else {
                System.out.printf("%-50s ✗ FAIL\n", entry.getKey());

                if (!result.versionMatches && result.spec.expectedVersion != null) {
                    System.out.printf("  Expected version: %d, got: %d\n",
                        result.spec.expectedVersion,
                        result.checkResult.requiredJavaVersion());
                }

                if (!result.missingRequired.isEmpty()) {
                    System.out.printf("  Missing required features: %s\n", result.missingRequired);
                }

                if (!result.presentForbidden.isEmpty()) {
                    System.out.printf("  Forbidden features present: %s\n", result.presentForbidden);
                }

                failed++;
            }
        }

        System.out.println("\n=== Summary ===");
        System.out.printf("Total: %d, Passed: %d, Failed: %d, Parse Failed: %d\n",
            results.size(), passed, failed, parseFailed);
        System.out.printf("Success rate: %.1f%%\n",
            (100.0 * passed / results.size()));
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            // Run all test directories
            List<File> testDirs = Arrays.asList(
                new File("feature_tests"),
                new File("feature_tests/edge_cases"),
                new File("feature_tests/edge_cases/tiny")
            );

            Map<String, TestResult> allResults = new LinkedHashMap<>();

            for (File dir : testDirs) {
                if (dir.exists() && dir.isDirectory()) {
                    System.out.println("\n=== Testing " + dir.getPath() + " ===\n");
                    Map<String, TestResult> results = runTestsInDirectory(dir);
                    printResults(results);
                    allResults.putAll(results);
                }
            }

            System.out.println("\n=== Overall Summary ===");
            printResults(allResults);

        } else {
            // Test specific directory
            File dir = new File(args[0]);
            if (!dir.exists() || !dir.isDirectory()) {
                System.err.println("Directory not found: " + args[0]);
                System.exit(1);
            }

            Map<String, TestResult> results = runTestsInDirectory(dir);
            printResults(results);
        }
    }
}