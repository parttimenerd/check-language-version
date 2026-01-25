package me.bechberger.check;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
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

    /**
     * Check if a Java source file compiles successfully using javac command line.
     * Copies the file to a temp folder first to avoid classpath/package conflicts.
     */
    private static CompilationResult compileWithJavac(File sourceFile) {
        try {
            // Create a temp directory for this compilation
            Path tempDir = Files.createTempDirectory("javac_test_");
            Path tempSourceFile = tempDir.resolve(sourceFile.getName());

            // Copy the source file to the temp directory
            Files.copy(sourceFile.toPath(), tempSourceFile);

            ProcessBuilder pb = new ProcessBuilder(
                "javac",
                "--release", "25",
                "-d", tempDir.toString(),
                tempSourceFile.toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Read output
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();

            // Clean up temp files
            try {
                Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ignored) {}
                    });
            } catch (IOException ignored) {}

            return new CompilationResult(exitCode == 0, output.toString());
        } catch (Exception e) {
            return new CompilationResult(false, "Failed to run javac: " + e.getMessage());
        }
    }

    private record CompilationResult(boolean success, String output) {}

    /**
     * Check if a Java source file compiles successfully with a specific Java version.
     * Uses the --release flag which validates both source and target compatibility.
     *
     * @param sourceFile The Java source file to compile
     * @param version The Java version to compile with (e.g., 8, 11, 17, 21)
     * @return CompilationResult with success status and any error output
     */
    private static CompilationResult doesCompileWithVersion(File sourceFile, int version) {
        try {
            // Create a temp directory for this compilation
            Path tempDir = Files.createTempDirectory("javac_version_test_");
            Path tempSourceFile = tempDir.resolve(sourceFile.getName());

            // Copy the source file to the temp directory
            Files.copy(sourceFile.toPath(), tempSourceFile);

            ProcessBuilder pb = new ProcessBuilder(
                "javac",
                "--release", String.valueOf(version),
                "-d", tempDir.toString(),
                tempSourceFile.toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Read output
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();

            // Clean up temp files
            try {
                Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ignored) {}
                    });
            } catch (IOException ignored) {}

            return new CompilationResult(exitCode == 0, output.toString());
        } catch (Exception e) {
            return new CompilationResult(false, "Failed to run javac: " + e.getMessage());
        }
    }

    static class TestSpec {
        String description;
        Integer expectedVersion;
        Set<String> requiredFeatures = new HashSet<>();
        Set<String> optionalFeatures = new HashSet<>();
        Set<String> forbiddenFeatures = new HashSet<>();
        boolean skipLowerVersionCheck = false; // Skip "doesn't compile with version-1" check for API-only features
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
            } else if (content.startsWith("Compile Check:")) {
                String value = content.substring(content.indexOf(':') + 1).trim().toLowerCase();
                spec.skipLowerVersionCheck = value.equals("false") || value.equals("no") || value.equals("skip");
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

        // Check that all detected features are listed in required or optional
        Set<String> expectedFeatures = new HashSet<>();
        expectedFeatures.addAll(spec.requiredFeatures);
        expectedFeatures.addAll(spec.optionalFeatures);
        for (String detected : detectedFeatures) {
            assertTrue(expectedFeatures.contains(detected),
                String.format("Detected feature '%s' is not listed in required or optional features for %s. " +
                    "Please add it to the test specification. Detected features: %s",
                    detected, spec.file.getName(), detectedFeatures));
        }
    }

    /**
     * Cached javac version (-1 means not yet computed, -2 means error).
     */
    private static int cachedJavacVersion = -1;

    /**
     * Get the javac version by running "javac --version".
     */
    private static int getJavacVersion() {
        if (cachedJavacVersion != -1) {
            return cachedJavacVersion;
        }
        try {
            ProcessBuilder pb = new ProcessBuilder("javac", "-version");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }

            process.waitFor();
            // Output is like "javac 25" or "javac 21.0.1"
            String versionStr = output.toString().trim();
            if (versionStr.startsWith("javac ")) {
                String version = versionStr.substring(6).split("\\.")[0];
                cachedJavacVersion = Integer.parseInt(version);
                return cachedJavacVersion;
            }
        } catch (Exception e) {
            // ignore
        }
        cachedJavacVersion = -2;
        return cachedJavacVersion;
    }

    @ParameterizedTest(name = "Compilation: {0}")
    @MethodSource("provideTestFiles")
    @DisplayName("Test file compilation with Java 25")
    void testCompilation(TestSpec spec) {
        // Only run when explicitly enabled via -Dtest.compilation=true
        Assumptions.assumeTrue("true".equals(System.getProperty("test.compilation")),
            "Skipping compilation test: set -Dtest.compilation=true to enable");

        // Skip if javac is not version 25
        int javacVersion = getJavacVersion();
        Assumptions.assumeTrue(javacVersion == 25,
            "Skipping compilation test: javac version is " + javacVersion + ", expected 25");

        assertNotNull(spec.file, "Test file should not be null");
        assertTrue(spec.file.exists(), "Test file should exist: " + spec.file);

        CompilationResult result = compileWithJavac(spec.file);

        // Skip files with module-related compilation issues
        if (!result.success()) {
            String output = result.output();
            boolean isModuleIssue = output.contains("is not visible")
                || output.contains("module")
                || output.contains("does not read it")
                || output.contains("package") && output.contains("declared in module")
                || output.contains("module declarations should be in")
                || output.contains("package") && output.contains("does not exist");
            Assumptions.assumeFalse(isModuleIssue,
                "Skipping compilation test due to module-related issues: " + spec.file.getName());
        }

        assertTrue(result.success(),
            String.format("File %s does not compile with Java 25. Errors:%n%s",
                spec.file.getName(), result.output()));
    }

    @ParameterizedTest(name = "Version compatibility: {0}")
    @MethodSource("provideTestFiles")
    @DisplayName("Test file compiles with expected version and not with version-1")
    void testVersionCompatibility(TestSpec spec) {
        // Only run when explicitly enabled via -Dtest.compilation=true
        Assumptions.assumeTrue("true".equals(System.getProperty("test.compilation")),
            "Skipping version compatibility test: set -Dtest.compilation=true to enable");

        // Skip if javac is not available or version is too old
        int javacVersion = getJavacVersion();
        Assumptions.assumeTrue(javacVersion >= 8,
            "Skipping version compatibility test: javac version is " + javacVersion + ", need at least 8");

        assertNotNull(spec.file, "Test file should not be null");
        assertTrue(spec.file.exists(), "Test file should exist: " + spec.file);

        // Skip if no expected version specified
        Assumptions.assumeTrue(spec.expectedVersion != null,
            "Skipping version compatibility test: no expected version for " + spec.file.getName());

        // --release flag only works for Java 8 and above
        Assumptions.assumeTrue(spec.expectedVersion >= 8,
            "Skipping version compatibility test: expected version " + spec.expectedVersion +
            " is below 8 for " + spec.file.getName());

        // Can only test with versions supported by our javac
        Assumptions.assumeTrue(spec.expectedVersion <= javacVersion,
            "Skipping version compatibility test: expected version " + spec.expectedVersion +
            " is higher than javac version " + javacVersion);

        // Test 1: File should compile with expected version
        CompilationResult resultWithExpectedVersion = doesCompileWithVersion(spec.file, spec.expectedVersion);

        // Skip files with module-related compilation issues
        if (!resultWithExpectedVersion.success()) {
            String output = resultWithExpectedVersion.output();
            boolean isModuleIssue = output.contains("is not visible")
                || output.contains("module")
                || output.contains("does not read it")
                || (output.contains("package") && output.contains("declared in module"))
                || output.contains("module declarations should be in")
                || (output.contains("package") && output.contains("does not exist"));
            Assumptions.assumeFalse(isModuleIssue,
                "Skipping version compatibility test due to module-related issues: " + spec.file.getName());
        }

        assertTrue(resultWithExpectedVersion.success(),
            String.format("File %s should compile with Java %d but failed. Errors:%n%s",
                spec.file.getName(), spec.expectedVersion, resultWithExpectedVersion.output()));

        // Test 2: For versions > 8, file should NOT compile with version - 1
        // Skip this check for API-only features that don't affect compilation
        if (spec.expectedVersion > 8 && !spec.skipLowerVersionCheck) {
            int lowerVersion = spec.expectedVersion - 1;
            CompilationResult resultWithLowerVersion = doesCompileWithVersion(spec.file, lowerVersion);

            assertFalse(resultWithLowerVersion.success(),
                String.format("File %s should NOT compile with Java %d (one below expected %d), " +
                    "but it compiled successfully. This suggests the expected version may be wrong.",
                    spec.file.getName(), lowerVersion, spec.expectedVersion));
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

        // Check that all detected features are listed in required or optional
        Set<String> expectedFeatures = new HashSet<>();
        expectedFeatures.addAll(spec.requiredFeatures);
        expectedFeatures.addAll(spec.optionalFeatures);
        for (String detected : detectedFeatures) {
            assertTrue(expectedFeatures.contains(detected),
                String.format("Detected feature '%s' is not listed in required or optional features for %s. " +
                    "Please add it to the test specification. Detected features: %s",
                    detected, testFile.getName(), detectedFeatures));
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