package me.bechberger.check;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Parameterized tests for Java feature detection using test files in resources.
 */
public class FeatureDetectionTest {

    static class TestSpec {
        String description;
        Integer expectedVersion;
        Set<String> requiredFeatures = new HashSet<>();
        Set<String> optionalFeatures = new HashSet<>();
        Set<String> forbiddenFeatures = new HashSet<>();
        File file;
        String testName;

        @Override
        public String toString() {
            return testName != null ? testName : file.getName();
        }
    }

    /**
     * Parse test specification from file comments
     */
    public static TestSpec parseTestSpec(File file) throws IOException {
        TestSpec spec = new TestSpec();
        spec.file = file;
        spec.testName = file.getName();

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
        if (features.isEmpty() || features.equals("NONE")) {
            return new HashSet<>();
        }
        return Arrays.stream(features.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());
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
     * Collect all test files from resources
     */
    private static Stream<TestSpec> provideTestFiles() throws IOException, URISyntaxException {
        List<TestSpec> specs = new ArrayList<>();

        // Get all test directories
        String[] testDirs = {
            "feature_tests",
            "feature_tests/edge_cases",
            "feature_tests/edge_cases/tiny"
        };

        for (String dir : testDirs) {
            URL resource = FeatureDetectionTest.class.getClassLoader().getResource(dir);
            if (resource != null) {
                File directory = new File(resource.toURI());
                if (directory.exists() && directory.isDirectory()) {
                    File[] files = directory.listFiles((d, name) -> name.endsWith(".java"));
                    if (files != null) {
                        for (File file : files) {
                            try {
                                specs.add(parseTestSpec(file));
                            } catch (IOException e) {
                                System.err.println("Failed to parse test spec for " + file.getName() + ": " + e.getMessage());
                            }
                        }
                    }
                }
            }
        }

        return specs.stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTestFiles")
    @DisplayName("Feature detection test")
    void testFeatureDetection(TestSpec spec) {
        assertNotNull(spec.file, "Test file should not be null");
        assertTrue(spec.file.exists(), "Test file should exist: " + spec.file);

        // Skip files that JavaParser cannot parse due to unsupported features
        Set<String> skipFiles = Set.of(
        );
        Assumptions.assumeFalse(skipFiles.contains(spec.file.getName()),
            "Skipping " + spec.file.getName() + " due to JavaParser parsing limitations");

        // Run feature detection
        FeatureChecker.FeatureCheckResult result;

        try {
            result = FeatureChecker.check(spec.file);
        } catch (FileNotFoundException e) {
            fail("Failed to parse file: " + spec.file.getName() + " - " + e.getMessage());
            return;
        }

        assertNotNull(result, "Parse should succeed for " + spec.file.getName());

        // Get detected feature names
        Set<String> detectedFeatures = result.features().stream()
            .map(FeatureChecker.JavaFeature::name)
            .collect(Collectors.toSet());

        // Check version if specified
        if (spec.expectedVersion != null) {
            int detectedVersion = result.requiredJavaVersion();
            assertEquals(spec.expectedVersion, detectedVersion,
                String.format("Expected Java %d, but detected Java %d for %s. Detected features: %s",
                    spec.expectedVersion, detectedVersion, spec.file.getName(), detectedFeatures));
        }

        // Check required features
        for (String required : spec.requiredFeatures) {
            assertTrue(detectedFeatures.contains(required),
                String.format("Required feature '%s' not detected in %s. Detected features: %s",
                    required, spec.file.getName(), detectedFeatures));
        }

        // Check forbidden features
        for (String forbidden : spec.forbiddenFeatures) {
            assertFalse(detectedFeatures.contains(forbidden),
                String.format("Forbidden feature '%s' was detected in %s",
                    forbidden, spec.file.getName()));
        }
    }

    // ==================== Helper Methods for Single Test File Tests ====================

    /**
     * Helper method to test a single test file.
     * Automatically parses test requirements from comments and validates them.
     *
     * @param filenameOrPath The filename (e.g., "Java8_ConcurrentAPI.java") which gets prefixed with "feature_tests/",
     *                       or a full path (e.g., "feature_tests/edge_cases/Test.java")
     * @param debugOutput Whether to print debug output showing detected features
     */
    private void testSingleFile(String filenameOrPath, boolean debugOutput) throws Exception {
        // If it doesn't contain a slash and doesn't start with "feature_tests", add the prefix
        String resourcePath = filenameOrPath;
        if (!filenameOrPath.contains("/") && !filenameOrPath.startsWith("feature_tests")) {
            resourcePath = "feature_tests/" + filenameOrPath;
        }

        // Load the test file
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        assertNotNull(resource, resourcePath + " test file not found");

        File testFile = new File(resource.toURI());
        assertTrue(testFile.exists(), "Test file should exist: " + resourcePath);

        // Parse test spec from comments
        TestSpec spec = parseTestSpec(testFile);

        // Run feature detection with appropriate minimum version
        FeatureChecker.FeatureCheckResult result = FeatureChecker.check(testFile);

        assertNotNull(result, "Parse should succeed for " + resourcePath);

        // Get detected feature names
        Set<String> detectedFeatures = result.features().stream()
            .map(FeatureChecker.JavaFeature::name)
            .collect(Collectors.toSet());

        // Debug output if requested
        if (debugOutput) {
            System.out.println("=== Testing: " + resourcePath + " ===");
            System.out.println("Expected version: " + spec.expectedVersion);
            System.out.println("Detected version: " + result.requiredJavaVersion());
            System.out.println("Required features: " + spec.requiredFeatures);
            System.out.println("Detected features: " + detectedFeatures);
            System.out.println();
        }

        // Check version if specified
        if (spec.expectedVersion != null) {
            int detectedVersion = result.requiredJavaVersion();
            assertEquals(spec.expectedVersion, detectedVersion,
                String.format("Expected Java %d, but detected Java %d for %s. Detected features: %s",
                    spec.expectedVersion, detectedVersion, testFile.getName(), detectedFeatures));
        }

        // Check required features
        for (String required : spec.requiredFeatures) {
            assertTrue(detectedFeatures.contains(required),
                String.format("Required feature '%s' not detected in %s. Detected features: %s",
                    required, testFile.getName(), detectedFeatures));
        }

        // Check optional features (just report, don't fail)
        if (debugOutput && !spec.optionalFeatures.isEmpty()) {
            for (String optional : spec.optionalFeatures) {
                if (detectedFeatures.contains(optional)) {
                    System.out.println("Optional feature detected: " + optional);
                }
            }
        }

        // Check forbidden features
        for (String forbidden : spec.forbiddenFeatures) {
            assertFalse(detectedFeatures.contains(forbidden),
                String.format("Forbidden feature '%s' was detected in %s. Detected features: %s",
                    forbidden, testFile.getName(), detectedFeatures));
        }
    }

    /**
     * Shorthand helper to test a single file without debug output.
     *
     * @param filenameOrPath The filename (e.g., "Java8_ConcurrentAPI.java") which gets prefixed with "feature_tests/",
     *                       or a full path (e.g., "feature_tests/edge_cases/Test.java")
     */
    private void testSingleFile(String filenameOrPath) throws Exception {
        testSingleFile(filenameOrPath, false);
    }
}